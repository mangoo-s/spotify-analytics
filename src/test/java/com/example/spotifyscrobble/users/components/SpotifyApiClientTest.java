package com.example.spotifyscrobble.users.service.components;

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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class) //Update tests with MockRestServiceServer and need to add callback tests
public class SpotifyApiClientTest {
    @Mock
    private SpotifyConnectionRepository spotifyConnectionRepo;

    @Mock
    private UserRepository userRepo;

    @Mock RestClient.Builder restClientBuilder;
    @Mock RestClient restClient;

    private SpotifyApiClient apiClient;

    @BeforeEach
    void setup() {
        when(restClientBuilder.build()).thenReturn(restClient);

        apiClient = new SpotifyApiClient(restClientBuilder, spotifyConnectionRepo, userRepo);
    }

    @Test
    void getRecentlyPlayedTracks_ReturnsOkOnSuccess(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");
        Artists artist = new Artists("artistSpotifyId", "Bladee");
        Track track = new Track("unreal", "trackSpotifyId", List.of(artist), 1L);
        Cursors cursor = new Cursors("after");
        Items item = new Items(track, "playedAt");
        GetRecentlyPlayedTracksResponse response = new GetRecentlyPlayedTracksResponse(List.of(item), cursor);

        RestClient.RequestHeadersUriSpec<?> requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        doReturn(requestSpec).when(restClient).get();
        doReturn(requestSpec).when(requestSpec).uri(anyString(), any(Function.class));
        doReturn(headersSpec).when(requestSpec).header(anyString(), anyString());
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(GetRecentlyPlayedTracksResponse.class)).thenReturn(response);

        CurrentlyPlayingResult result = apiClient.getRecentlyPlayedTracks(connection, "after");

        assertThat(result.status()).isEqualTo(CurrentlyPlayingResult.Status.OK);

    }

    @Test
    void getRecentlyPlayedTracks_ReturnsAuthFailedOn401(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");

        RestClient.RequestHeadersUriSpec<?> requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);

        HttpClientErrorException.Unauthorized unauthorized = (HttpClientErrorException.Unauthorized)
                HttpClientErrorException.create(
                        HttpStatus.UNAUTHORIZED, "Unauthorized", HttpHeaders.EMPTY, new byte[0], null
                );

        doReturn(requestSpec).when(restClient).get();
        doReturn(requestSpec).when(requestSpec).uri(anyString(), any(Function.class));
        doReturn(headersSpec).when(requestSpec).header(anyString(), anyString());
        when(headersSpec.retrieve()).thenThrow(unauthorized);

        CurrentlyPlayingResult res = apiClient.getRecentlyPlayedTracks(connection, "after");

        assertThat(res.status()).isEqualTo(CurrentlyPlayingResult.Status.AUTH_FAILED);

    }

    @Test
    void getRecentlyPlayedTracks_ReturnsTransientFailureOn429(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");

        RestClient.RequestHeadersUriSpec<?> requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        HttpClientErrorException.TooManyRequests tooManyRequests = (HttpClientErrorException.TooManyRequests)
                HttpClientErrorException.create(
                        HttpStatus.TOO_MANY_REQUESTS, "Too many requests", HttpHeaders.EMPTY, new byte[0], null
                );

        doReturn(requestSpec).when(restClient).get();
        doReturn(requestSpec).when(requestSpec).uri(anyString(), any(Function.class));
        doReturn(headersSpec).when(requestSpec).header(anyString(), anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        when(responseSpec.body(GetRecentlyPlayedTracksResponse.class)).thenThrow(tooManyRequests);

        CurrentlyPlayingResult res = apiClient.getRecentlyPlayedTracks(connection, "after");

        assertThat(res.status()).isEqualTo(CurrentlyPlayingResult.Status.TRANSIENT_FAILURE);
    }

    @Test
    void getRecentlyPlayedTracks_ReturnsTransientFailureOnExtras(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");

        RestClient.RequestHeadersUriSpec<?> requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);


        doReturn(requestSpec).when(restClient).get();
        doReturn(requestSpec).when(requestSpec).uri(anyString(), any(Function.class));
        doReturn(headersSpec).when(requestSpec).header(anyString(), anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        when(responseSpec.body(GetRecentlyPlayedTracksResponse.class)).thenThrow(new ResourceAccessException("connection timed out"));

        CurrentlyPlayingResult res = apiClient.getRecentlyPlayedTracks(connection, "after");

        assertThat(res.status()).isEqualTo(CurrentlyPlayingResult.Status.TRANSIENT_FAILURE);
    }

    @Test
    void getRecentlyPlayedTracks_ReturnsTransientFailureOnEmptyResult(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");
        Artists artist = new Artists("artistSpotifyId", "Bladee");
        Track track = new Track("unreal", "trackSpotifyId", List.of(artist), 1L);
        Cursors cursor = new Cursors("after");
        Items item = new Items(track, "playedAt");
        GetRecentlyPlayedTracksResponse response = new GetRecentlyPlayedTracksResponse(null, null);

        RestClient.RequestHeadersUriSpec<?> requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        doReturn(requestSpec).when(restClient).get();
        doReturn(requestSpec).when(requestSpec).uri(anyString(), any(Function.class));
        doReturn(headersSpec).when(requestSpec).header(anyString(), anyString());
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(GetRecentlyPlayedTracksResponse.class)).thenReturn(null);

        CurrentlyPlayingResult res = apiClient.getRecentlyPlayedTracks(connection, "after");

        assertThat(res.status()).isEqualTo(CurrentlyPlayingResult.Status.TRANSIENT_FAILURE);
    }

    @Test
    void getRecentlyPlayedTracks_ReturnsAuthFailedOn403(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity connection = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");
        Artists artist = new Artists("artistSpotifyId", "Bladee");
        Track track = new Track("unreal", "trackSpotifyId", List.of(artist), 1L);
        Cursors cursor = new Cursors("after");
        Items item = new Items(track, "playedAt");
        GetRecentlyPlayedTracksResponse response = new GetRecentlyPlayedTracksResponse(List.of(item), cursor);

        RestClient.RequestHeadersUriSpec<?> requestSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec<?> headersSpec = mock(RestClient.RequestHeadersSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        HttpClientErrorException.Forbidden forbidden = (HttpClientErrorException.Forbidden)
                HttpClientErrorException.create(
                        HttpStatus.FORBIDDEN, "Forbidden", HttpHeaders.EMPTY, new byte[0], null
                );

        doReturn(requestSpec).when(restClient).get();
        doReturn(requestSpec).when(requestSpec).uri(anyString(), any(Function.class));
        doReturn(headersSpec).when(requestSpec).header(anyString(), anyString());
        doReturn(responseSpec).when(headersSpec).retrieve();
        when(responseSpec.body(GetRecentlyPlayedTracksResponse.class)).thenThrow(forbidden);

        CurrentlyPlayingResult res = apiClient.getRecentlyPlayedTracks(connection, "after");

        assertThat(res.status()).isEqualTo(CurrentlyPlayingResult.Status.AUTH_FAILED);
    }


    @Test
    void getNewJwt_UpdatesTokenOnSuccess(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity spotifyUser = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");

        SpotifyAccessTokenResponse tokenResponse = new SpotifyAccessTokenResponse("newAccessToken", "newRefreshToken", 3600);

        RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(MultiValueMap.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SpotifyAccessTokenResponse.class)).thenReturn(tokenResponse);

        apiClient.getNewJwt(spotifyUser);
        assertThat(spotifyUser.getAccessToken()).isEqualTo("newAccessToken");
        verify(spotifyConnectionRepo).save(spotifyUser);


    }

    @Test
    void getNewJwt_ThrowsReAuthRequiredOn401(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity spotifyUser = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");

        RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);

        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(MultiValueMap.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenThrow(HttpClientErrorException.Unauthorized.class);

        assertThrows(SpotifyReauthRequiredException.class, () -> apiClient.getNewJwt(spotifyUser));

        verify(spotifyConnectionRepo, never()).save(any());
    }

    @Test
    void getNewJwt_ThrowsTokenRefreshExceptionOnServerError(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity spotifyUser = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");

        RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);

        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(MultiValueMap.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenThrow(HttpClientErrorException.class);

        assertThrows(SpotifyTokenRefreshException.class, () -> apiClient.getNewJwt(spotifyUser));

        verify(spotifyConnectionRepo, never()).save(any());
    }

    @Test
    void getNewJwt_ThrowsSpotifyRefreshTokenExceptionWhenSpotifyReturnsNullRefreshToken(){
        UserEntity user = new UserEntity(UUID.randomUUID(), "test@gmail.com");
        SpotifyConnectionEntity spotifyUser = new SpotifyConnectionEntity(user, 1, "accessToken", "refreshToken");
        SpotifyAccessTokenResponse tokenResponse = new SpotifyAccessTokenResponse(null, "newRefreshToken", 3600);

        RestClient.RequestBodyUriSpec uriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec bodySpec = mock(RestClient.RequestBodySpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);


        when(restClient.post()).thenReturn(uriSpec);
        when(uriSpec.uri(anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.header(anyString(), anyString())).thenReturn(bodySpec);
        when(bodySpec.body(any(MultiValueMap.class))).thenReturn(bodySpec);
        when(bodySpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(SpotifyAccessTokenResponse.class)).thenReturn(tokenResponse);

        assertThrows(SpotifyTokenRefreshException.class, () -> apiClient.getNewJwt(spotifyUser));

        verify(spotifyConnectionRepo, never()).save(any());


    }
}
