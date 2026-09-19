package com.example.spotifyscrobble.users.components;

import com.example.spotifyscrobble.users.dto.GetRecentlyPlayedTracksResponse;
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
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.*;

import java.time.Instant;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class SpotifyApiClient {
    private final RestClient restClient;
    private final SpotifyConnectionRepository spotifyConnectionRepo;
    private final UserRepository userRepo;

    @Value("${SPOTIFY_CLIENT_ID}")
    private String clientId;

    @Value("${SPOTIFY_CLIENT_SECRET}")
    private String clientSecret;

    public SpotifyApiClient(RestClient.Builder restClientBuilder, SpotifyConnectionRepository spotifyConnectionRepo, UserRepository userRepo) {
        this.restClient = restClientBuilder.build();
        this.spotifyConnectionRepo = spotifyConnectionRepo;
        this.userRepo = userRepo;
    }


    public CurrentlyPlayingResult getRecentlyPlayedTracks(SpotifyConnectionEntity user, String after){

        try{
            GetRecentlyPlayedTracksResponse response = restClient.get()
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
        }catch(ResourceAccessException e){
            log.warn("Spotify failed for user {}", user.getUserId(), e);
            return CurrentlyPlayingResult.transientFailure();
        }
    }

    public void getNewJwt(SpotifyConnectionEntity user){ //Need to create restclient config
        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        body.add("grant_type", "refresh_token");
        body.add("refresh_token", user.getRefreshToken());
        SpotifyAccessTokenResponse tokenResponse;
        try{
            tokenResponse = restClient.post()
                    .uri("https://accounts.spotify.com/api/token")
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic "+ credentials)
                    .body(body)
                    .retrieve()
                    .body(SpotifyAccessTokenResponse.class);

        }catch(HttpClientErrorException.Unauthorized | HttpClientErrorException.BadRequest e){
            throw new SpotifyReauthRequiredException("Refresh token invalid for user " + user.getUserId() + ". User needs to login again.", e);
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
        System.out.println("Hello");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", "http://127.0.0.1:8080/callback");

        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        SpotifyAccessTokenResponse tokenResponse = restClient.post()
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
