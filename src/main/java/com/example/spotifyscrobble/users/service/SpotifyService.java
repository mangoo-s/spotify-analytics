package com.example.spotifyscrobble.users.service;

import com.example.spotifyscrobble.listening.TrackListenedEvent;
import com.example.spotifyscrobble.users.components.SpotifyApiClient;
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
    private final ApplicationEventPublisher events;
    private final SpotifyApiClient apiClient;

    public SpotifyService(SpotifyConnectionRepository spotifyConnectionRepo, ApplicationEventPublisher events, SpotifyApiClient apiClient) {
        this.spotifyConnectionRepo = spotifyConnectionRepo;
        this.events = events;
        this.apiClient = apiClient;
    }

    @Scheduled(fixedDelay = 300000)
    @Transactional
    public void processPlays(){
        List<SpotifyConnectionEntity> spotifyConnection = spotifyConnectionRepo.findAll();

        for(SpotifyConnectionEntity user: spotifyConnection){
            if(Instant.now().isAfter(user.getExpiresIn())){
                try{
                    apiClient.getNewJwt(user);
                }catch(SpotifyReauthRequiredException e){
                    log.warn("User {} needs to reconnect Spotify", user.getUserId());
                    continue;
                }catch(SpotifyTokenRefreshException e){
                    log.error("Refreshing token had an error for user {}", user.getUserId());
                    continue;
                }
            }
            CurrentlyPlayingResult currentlyPlayingTrackResponse = apiClient.getRecentlyPlayedTracks(user, user.getAfter());


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


}
