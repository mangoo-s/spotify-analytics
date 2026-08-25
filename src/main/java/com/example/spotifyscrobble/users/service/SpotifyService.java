package com.example.spotifyscrobble.users.service;

import com.example.spotifyscrobble.listening.TrackListenedEvent;
import com.example.spotifyscrobble.users.dto.GetRecentlyPlayedTracksResponse;
import com.example.spotifyscrobble.users.dto.Items;
import com.example.spotifyscrobble.users.dto.SpotifyAccessTokenResponse;
import com.example.spotifyscrobble.users.entity.SpotifyConnectionEntity;
import com.example.spotifyscrobble.users.entity.UserEntity;
import com.example.spotifyscrobble.users.exceptions.SpotifyReauthRequiredException;
import com.example.spotifyscrobble.users.exceptions.SpotifyTokenRefreshException;
import com.example.spotifyscrobble.users.other.CurrentlyPlayingResult;
import com.example.spotifyscrobble.users.repository.SpotifyConnectionRepository;
import com.example.spotifyscrobble.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class SpotifyService {

    @Value("${SPOTIFY_CLIENT_ID}")
    private String clientId;

    @Value("${SPOTIFY_CLIENT_SECRET}")
    private String clientSecret;

    private final SpotifyConnectionRepository spotifyConnectionRepo;
    private final UserRepository userRepo;
    private final ApplicationEventPublisher events;

    public SpotifyService(SpotifyConnectionRepository spotifyConnectionRepo, UserRepository userRepo, ApplicationEventPublisher events) {
        this.spotifyConnectionRepo = spotifyConnectionRepo;
        this.userRepo = userRepo;
        this.events = events;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void processPlays(){
        List<SpotifyConnectionEntity> spotifyConnection = spotifyConnectionRepo.findAll();

        for(SpotifyConnectionEntity user: spotifyConnection){
            if(Instant.now().isAfter(user.getExpiresIn())){
                try{
                    getNewJwt(user);
                }catch(SpotifyReauthRequiredException e){
                    log.warn("User {} needs to reconnect Spotify", user.getUserId());
                    continue;
                }catch(SpotifyTokenRefreshException e){
                    log.error("Refreshing token had an error for user {}", user.getUserId());
                    continue;
                }
            }
            CurrentlyPlayingResult currentlyPlayingTrackResponse = getRecentlyPlayedTracks(user, user.getAfter());


            switch(currentlyPlayingTrackResponse.status()){
                case OK ->{
                    List<Items> items = currentlyPlayingTrackResponse.response().items();

                    for(Items tracks: currentlyPlayingTrackResponse.response().items()){
                        events.publishEvent(
                                new TrackListenedEvent(
                                        user.getUserId(),
                                        user.getUser().getUsername(),
                                        tracks.item().spotifyId(),
                                        tracks.item().artists().get(0).spotifyId(),
                                        Instant.parse(tracks.played_at()),
                                        tracks.item().artists().get(0).name(),
                                        tracks.item().name(),
                                        tracks.item().duration()
                                )
                        );
                    }
                    if (!items.isEmpty()) {
                        // items[0] is the most recently played track
                        Instant mostRecentPlayedAt = Instant.parse(items.get(0).played_at());
                        user.setAfter(String.valueOf(mostRecentPlayedAt.toEpochMilli()));
                        spotifyConnectionRepo.save(user);
                    }
                }
                case TRANSIENT_FAILURE -> { }
                case AUTH_FAILED -> {
                    System.out.println("refresh token dont exist so need to log in");
                }
            }

        }
        log.info("Plays at {} have been processed.", Instant.now().truncatedTo(ChronoUnit.MINUTES).toString());
    }

    public CurrentlyPlayingResult getRecentlyPlayedTracks(SpotifyConnectionEntity user, String after){
        RestClient rc = RestClient.create();

        try{
            GetRecentlyPlayedTracksResponse response = rc.get()
                    .uri("https://api.spotify.com/v1/me/player/recently-played", uriBuilder -> uriBuilder
                            .queryParamIfPresent("after", Optional.ofNullable(after)
                                    .map(Long::parseLong))
                            .build())
                    .header("Authorization", "Bearer "+user.getAccessToken())
                    .retrieve()
                    .body(GetRecentlyPlayedTracksResponse.class);
            if (response == null) {
                log.warn("Empty response body for user {}", user.getUserId());
                return CurrentlyPlayingResult.transientFailure();
            }
            return CurrentlyPlayingResult.ok(response);

        }catch(HttpClientErrorException.Unauthorized e){
            log.warn("Auth issue for user {} :{}", user.getUserId(), e.getResponseBodyAsString());
            return CurrentlyPlayingResult.authFailed();
        }catch(HttpClientErrorException.Forbidden e){
            log.warn("Oauth issue for user {}: {}", user.getUserId(), e.getResponseBodyAsString());
            return CurrentlyPlayingResult.authFailed();
        }catch(HttpClientErrorException.TooManyRequests e){
            log.warn("Rate limit reached");
            return CurrentlyPlayingResult.transientFailure();
        }catch(RestClientException e){
            log.warn("Spotify failed for user {}", user.getUserId(), e);
            return CurrentlyPlayingResult.transientFailure();
        }
    }

    public void getNewJwt(SpotifyConnectionEntity user){ //Need to create restclient config
        RestClient rc = RestClient.create();
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "refresh_token");
        body.add("refresh_token", user.getRefreshToken());
        SpotifyAccessTokenResponse tokenResponse;
        try{
            tokenResponse = rc.post()
                    .uri("https://accounts.spotify.com/api/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic "+ credentials)
                    .body(body)
                    .retrieve()
                    .body(SpotifyAccessTokenResponse.class);

        }catch(HttpClientErrorException.Unauthorized | HttpClientErrorException.BadRequest e){
            throw new SpotifyReauthRequiredException("Refresh token invalid for user " + user.getUserId(), e);
        }catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e){
            throw new SpotifyTokenRefreshException("Token refresh failed for user " + user.getUserId(), e);
        }

        if (tokenResponse == null || tokenResponse.access_token() == null) {
            throw new SpotifyTokenRefreshException("Spotify returned an empty token response");
        }

        user.setAccessToken(tokenResponse.access_token());
        user.setExpiresIn(Instant.now().plusSeconds(tokenResponse.expiresIn()));
        spotifyConnectionRepo.save(user);
    }

    public void spotifyCallback(String code, String state){ //Needs error handling too
        RestClient rc = RestClient.create();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", "http://127.0.0.1:8080/callback");

        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        SpotifyAccessTokenResponse tokenResponse = rc.post()
                .uri("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic "+credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(body)
                .retrieve()
                .body(SpotifyAccessTokenResponse.class);
        UserEntity user = userRepo.getReferenceById(UUID.fromString(state));
        SpotifyConnectionEntity connectionEntity = new SpotifyConnectionEntity(user, tokenResponse.expiresIn(), tokenResponse.access_token(), tokenResponse.refresh_token());
        spotifyConnectionRepo.save(connectionEntity);
    }
}
