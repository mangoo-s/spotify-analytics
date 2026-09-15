package com.example.spotifyscrobble.users.service;

import com.example.spotifyscrobble.listening.TrackListenedEvent;
import com.example.spotifyscrobble.users.components.SpotifyApiClient;
import com.example.spotifyscrobble.users.dto.*;
import com.example.spotifyscrobble.users.entity.SpotifyConnectionEntity;
import com.example.spotifyscrobble.users.entity.UserEntity;
import com.example.spotifyscrobble.users.exceptions.SpotifyReauthRequiredException;
import com.example.spotifyscrobble.users.exceptions.SpotifyTokenRefreshException;
import com.example.spotifyscrobble.users.other.CurrentlyPlayingResult;
import com.example.spotifyscrobble.users.repository.SpotifyConnectionRepository;
import com.example.spotifyscrobble.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpotifyServiceTest {

    @Mock
    private SpotifyConnectionRepository spotifyConnectionRepo;


    @Mock
    private ApplicationEventPublisher events;


    @Mock
    private SpotifyApiClient apiClient;

    @InjectMocks
    private SpotifyService spotifyService;


    @Test
    void processPlays_ContinuesWhenReAuthRequired(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, -1, "accessToken", "refreshToken");

        when(spotifyConnectionRepo.findAll()).thenReturn(List.of(connection));
        doThrow(new SpotifyReauthRequiredException("expired", null)).when(apiClient).getNewJwt(connection);

        spotifyService.processPlays();

        verify(apiClient, never()).getRecentlyPlayedTracks(any(), any());
        verify(events, never()).publishEvent(any());

    }

    @Test
    void processPlays_PublishesEventAndUpdatesAfterSuccess(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, 3600, "accessToken", "refreshToken");
        Artists artist = new Artists("artistSpotifyId", "Bladee");
        Track track = new Track("unreal", "trackSpotifyId", List.of(artist), 1L);
        Cursors cursor = new Cursors("after");
        Items item = new Items(track, "2026-09-15T10:03:35Z");
        GetRecentlyPlayedTracksResponse response = new GetRecentlyPlayedTracksResponse(List.of(item), cursor);
        CurrentlyPlayingResult result = new CurrentlyPlayingResult(response, CurrentlyPlayingResult.Status.OK);

        when(spotifyConnectionRepo.findAll()).thenReturn(List.of(connection));
        when(apiClient.getRecentlyPlayedTracks(connection, connection.getAfter())).thenReturn(result);

        spotifyService.processPlays();

        verify(events).publishEvent(any(TrackListenedEvent.class));
        verify(spotifyConnectionRepo).save(connection);

    }

    @Test
    void processPlays_DoesNothingOnTransientFailure(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, 3600, "accessToken", "refreshToken");
        Artists artist = new Artists("artistSpotifyId", "Bladee");
        Track track = new Track("unreal", "trackSpotifyId", List.of(artist), 1L);
        Cursors cursor = new Cursors("after");
        Items item = new Items(track, "2026-09-15T10:03:35Z");
        GetRecentlyPlayedTracksResponse response = new GetRecentlyPlayedTracksResponse(List.of(item), cursor);
        CurrentlyPlayingResult result = new CurrentlyPlayingResult(response, CurrentlyPlayingResult.Status.TRANSIENT_FAILURE);

        when(spotifyConnectionRepo.findAll()).thenReturn(List.of(connection));
        when(apiClient.getRecentlyPlayedTracks(connection, connection.getAfter())).thenReturn(result);

        spotifyService.processPlays();;

        verify(events, never()).publishEvent(any(TrackListenedEvent.class));
        verify(spotifyConnectionRepo, never()).save(connection);
    }

    @Test
    void processPlays_EnsuresSpotifyTokenRefreshExceptionIsCaught(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, -1, "accessToken", "refreshToken");


        when(spotifyConnectionRepo.findAll()).thenReturn(List.of(connection));
        doThrow(new SpotifyTokenRefreshException("refresh failure", null)).when(apiClient).getNewJwt(connection);

        spotifyService.processPlays();

        verify(apiClient, never()).getRecentlyPlayedTracks(any(), any());
        verify(events, never()).publishEvent(any());
    }


}
