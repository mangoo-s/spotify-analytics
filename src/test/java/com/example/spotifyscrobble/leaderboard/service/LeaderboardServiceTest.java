package com.example.spotifyscrobble.leaderboard.service;

import com.example.spotifyscrobble.catalog.ArtistDeletedEvent;
import com.example.spotifyscrobble.catalog.CatalogApi;
import com.example.spotifyscrobble.catalog.GetArtistAndTrackbyTrackIdDto;
import com.example.spotifyscrobble.catalog.TrackDeletedEvent;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardArtistEntry;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardGlobalArtistEntry;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardGlobalTrackEntry;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardUserEntry;
import com.example.spotifyscrobble.shared.ArtistNotFoundException;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.shared.TrackNotFoundException;
import com.example.spotifyscrobble.users.UsersApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisZSetCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LeaderboardServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private UsersApi usersApi;

    @Mock
    private CatalogApi catalogApi;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @InjectMocks
    LeaderboardService leaderboardService;

    @Test
    void recordPlay_ExecutesPipelinedCommands(){
        UUID userId = UUID.randomUUID();
        doReturn(Collections.emptyList()).when(redisTemplate).executePipelined(any(RedisCallback.class));

        leaderboardService.recordPlay(1L, 2L, userId);

        verify(redisTemplate).executePipelined(any(RedisCallback.class));
    }

    @Test
    void recordPlay_IncrementAllFiveZSets(){
        UUID userId = UUID.randomUUID();
        RedisConnection connection = mock(RedisConnection.class);
        RedisZSetCommands zSetCommands = mock(RedisZSetCommands.class);
        doReturn(zSetCommands).when(connection).zSetCommands();

        ArgumentCaptor<RedisCallback<?>> callbackCaptor = ArgumentCaptor.forClass(RedisCallback.class);
        doReturn(Collections.emptyList()).when(redisTemplate).executePipelined(callbackCaptor.capture());

        leaderboardService.recordPlay(1L, 2L, userId);

        assertDoesNotThrow(() -> callbackCaptor.getValue().doInRedis(connection));
        verify(zSetCommands, times(5)).zIncrBy(any(byte[].class), eq(1.0), any(byte[].class));
    }

    @Test
    void getTopArtistListeners_ReturnsPopulatedLeaderboard() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn(userId.toString());
        when(tuple.getScore()).thenReturn(5.0);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores("leaderboard:artist:1", 0, 9))
                .thenReturn(Set.of(tuple));
        when(catalogApi.getArtistNameById(1L)).thenReturn("artist");
        when(usersApi.getUsernameByUserId(userId)).thenReturn("testUser");
        when(zSetOperations.zCard(anyString())).thenReturn(1L);


        CustomPageResponse<LeaderboardArtistEntry> result =
                leaderboardService.getTopArtistListeners(1L, pageable);

        assertThat(result.content().size()).isEqualTo(1);
        LeaderboardArtistEntry entry = result.content().get(0);
        assertThat(entry.artistName()).isEqualTo("artist");
        assertThat(entry.username()).isEqualTo("testUser");
        assertThat(entry.score()).isEqualTo(5.0);
        assertThat(entry.rank()).isEqualTo(1);
    }

    @Test
    void getTopArtistListeners_ReturnsEmptyWhenNoResults() {
        Pageable pageable = PageRequest.of(0, 10);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores("leaderboard:artist:1", 0, 9))
                .thenReturn(Collections.emptySet());
        when(zSetOperations.zCard(anyString())).thenReturn(0L);

        CustomPageResponse<LeaderboardArtistEntry> result =
                leaderboardService.getTopArtistListeners(1L, pageable);

        assertThat(result.content().isEmpty()).isTrue();
        verifyNoInteractions(catalogApi);
    }

    @Test
    void getTopArtistListeners_SkipsEntriesWithNullValueOrScore() {
        Pageable pageable = PageRequest.of(0, 10);

        ZSetOperations.TypedTuple<Object> badTuple = mock(ZSetOperations.TypedTuple.class);
        when(badTuple.getValue()).thenReturn(null);
        when(badTuple.getScore()).thenReturn(5.0);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores("leaderboard:artist:1", 0, 9))
                .thenReturn(Set.of(badTuple));
        when(catalogApi.getArtistNameById(1L)).thenReturn("artist");
        when(zSetOperations.zCard(anyString())).thenReturn(1L);

        CustomPageResponse<LeaderboardArtistEntry> result =
                leaderboardService.getTopArtistListeners(1L, pageable);

        assertThat(result.content().isEmpty()).isTrue();
        verifyNoInteractions(usersApi);
    }

    @Test
    void getTopArtistListeners_ThrowsWhenArtistNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);

        doReturn(zSetOperations).when(redisTemplate).opsForZSet();
        doReturn(Set.of(tuple)).when(zSetOperations).reverseRangeWithScores("leaderboard:artist:1", 0, 9);
        doThrow(new ArtistNotFoundException("This artist does not exist"))
                .when(catalogApi).getArtistNameById(1L);

        assertThrows(ArtistNotFoundException.class,
                () -> leaderboardService.getTopArtistListeners(1L, pageable));

        verifyNoInteractions(usersApi);
    }

    @Test
    void getTopTrackListeners_ReturnsPopulatedLeaderboard() {
        UUID userId = UUID.randomUUID();
        Pageable pageable = PageRequest.of(0, 10);

        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn(userId.toString());
        when(tuple.getScore()).thenReturn(3.0);

        GetArtistAndTrackbyTrackIdDto dto = new GetArtistAndTrackbyTrackIdDto("artist", "trackName");

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores("leaderboard:track:5", 0, 9))
                .thenReturn(Set.of(tuple));
        when(catalogApi.getArtistAndTrackByTrackId(5L)).thenReturn(dto);
        when(usersApi.getUsernameByUserId(userId)).thenReturn("testUser");
        when(zSetOperations.zCard(anyString())).thenReturn(1L);

        CustomPageResponse<LeaderboardUserEntry> result =
                leaderboardService.getTopTrackListeners(5L, pageable);

        assertThat(result.content().size()).isEqualTo(1);
        LeaderboardUserEntry entry = result.content().get(0);
        assertThat(entry.username()).isEqualTo("testUser");
        assertThat(entry.artistName()).isEqualTo("artist");
        assertThat(entry.trackName()).isEqualTo("trackName");
        assertThat(entry.score()).isEqualTo(3.0);
    }

    @Test
    void getTopTrackListeners_ReturnsEmptyWhenNoResults() {
        Pageable pageable = PageRequest.of(0, 10);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores("leaderboard:track:5", 0, 9))
                .thenReturn(Collections.emptySet());
        when(zSetOperations.zCard(anyString())).thenReturn(0L);

        CustomPageResponse<LeaderboardUserEntry> result =
                leaderboardService.getTopTrackListeners(5L, pageable);

        assertThat(result.content().isEmpty()).isTrue();
        verifyNoInteractions(catalogApi);
    }

    @Test
    void getTopTrackListeners_ThrowsWhenTrackNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);

        doReturn(zSetOperations).when(redisTemplate).opsForZSet();
        doReturn(Set.of(tuple)).when(zSetOperations).reverseRangeWithScores("leaderboard:track:5", 0, 9);
        doThrow(new TrackNotFoundException("This track does not exist"))
                .when(catalogApi).getArtistAndTrackByTrackId(5L);

        assertThrows(TrackNotFoundException.class,
                () -> leaderboardService.getTopTrackListeners(5L, pageable));

        verifyNoInteractions(usersApi);
    }

    @Test
    void getTopGlobalTracks_ReturnsPopulatedLeaderboard() {
        Pageable pageable = PageRequest.of(0, 10);

        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn("5");
        when(tuple.getScore()).thenReturn(10.0);

        GetArtistAndTrackbyTrackIdDto dto = new GetArtistAndTrackbyTrackIdDto("artist", "trackName");

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores("leaderboard:global:tracks", 0, 9))
                .thenReturn(Set.of(tuple));
        when(catalogApi.getArtistAndTrackByTrackId(5L)).thenReturn(dto);
        when(zSetOperations.zCard("leaderboard:global:tracks")).thenReturn(1L);

        CustomPageResponse<LeaderboardGlobalTrackEntry> result =
                leaderboardService.getTopGlobalTracks(pageable);

        assertThat(result.content().size()).isEqualTo(1);
        assertThat(result.content().get(0).artistName()).isEqualTo("artist");
        assertThat(result.content().get(0).trackName()).isEqualTo("trackName");
    }

    @Test
    void getTopGlobalTracks_ReturnsEmptyWhenNoResults() {
        Pageable pageable = PageRequest.of(0, 10);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores("leaderboard:global:tracks", 0, 9))
                .thenReturn(Collections.emptySet());
        when(zSetOperations.zCard("leaderboard:global:tracks")).thenReturn(0L);

        CustomPageResponse<LeaderboardGlobalTrackEntry> result =
                leaderboardService.getTopGlobalTracks(pageable);

        assertThat(result.content().isEmpty()).isTrue();
    }

    @Test
    void getTopGlobalTracks_SkipsEntryWhenArtistNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        ZSetOperations.TypedTuple<Object> missingArtist = mock(ZSetOperations.TypedTuple.class);
        when(missingArtist.getValue()).thenReturn("1");
        when(missingArtist.getScore()).thenReturn(8.0);

        ZSetOperations.TypedTuple<Object> validArtist = mock(ZSetOperations.TypedTuple.class);
        when(validArtist.getValue()).thenReturn("2");
        when(validArtist.getScore()).thenReturn(5.0);

        GetArtistAndTrackbyTrackIdDto dto = new GetArtistAndTrackbyTrackIdDto("artist", "trackName");

        doReturn(zSetOperations).when(redisTemplate).opsForZSet();
        doReturn(new LinkedHashSet<>(List.of(missingArtist, validArtist)))
                .when(zSetOperations).reverseRangeWithScores("leaderboard:global:tracks", 0, 9);
        doThrow(new TrackNotFoundException("does not exist")).when(catalogApi).getArtistAndTrackByTrackId(1L);
        when(catalogApi.getArtistAndTrackByTrackId(2L)).thenReturn(dto);
        when(zSetOperations.zCard(anyString())).thenReturn(2L);

        CustomPageResponse<LeaderboardGlobalTrackEntry> result =
                leaderboardService.getTopGlobalTracks(pageable);

        assertThat(result.content().size()).isEqualTo(1);
        assertThat(result.content().get(0).artistName()).isEqualTo("artist");
        assertThat(result.content().get(0).trackName()).isEqualTo("trackName");
    }

    @Test
    void getTopGlobalArtists_ReturnsPopulatedLeaderboard() {
        Pageable pageable = PageRequest.of(0, 10);

        ZSetOperations.TypedTuple<Object> tuple = mock(ZSetOperations.TypedTuple.class);
        when(tuple.getValue()).thenReturn("1");
        when(tuple.getScore()).thenReturn(8.0);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores("leaderboard:global:artists", 0, 9))
                .thenReturn(Set.of(tuple));
        when(catalogApi.getArtistNameById(1L)).thenReturn("artist");

        when(zSetOperations.zCard("leaderboard:global:artists")).thenReturn(1L);

        CustomPageResponse<LeaderboardGlobalArtistEntry> result =
                leaderboardService.getTopGlobalArtists(pageable);

        assertThat(result.content().size()).isEqualTo(1);
        assertThat(result.content().get(0).artistName()).isEqualTo("artist");
        assertThat(result.content().get(0).score()).isEqualTo(8.0);
    }

    @Test
    void getTopGlobalArtists_ReturnsEmptyWhenNoResults() {
        Pageable pageable = PageRequest.of(0, 10);

        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
        when(zSetOperations.reverseRangeWithScores("leaderboard:global:artists", 0, 9))
                .thenReturn(Collections.emptySet());
        when(zSetOperations.zCard("leaderboard:global:artists")).thenReturn(0L);

        CustomPageResponse<LeaderboardGlobalArtistEntry> result =
                leaderboardService.getTopGlobalArtists(pageable);

        assertThat(result.content().isEmpty()).isTrue();
    }

    @Test
    void getTopGlobalArtists_SkipsEntryWhenArtistNotFound() {
        Pageable pageable = PageRequest.of(0, 10);

        ZSetOperations.TypedTuple<Object> missingArtist = mock(ZSetOperations.TypedTuple.class);
        when(missingArtist.getValue()).thenReturn("1");
        when(missingArtist.getScore()).thenReturn(8.0);

        ZSetOperations.TypedTuple<Object> validArtist = mock(ZSetOperations.TypedTuple.class);
        when(validArtist.getValue()).thenReturn("2");
        when(validArtist.getScore()).thenReturn(5.0);

        doReturn(zSetOperations).when(redisTemplate).opsForZSet();
        doReturn(new LinkedHashSet<>(List.of(missingArtist, validArtist)))
                .when(zSetOperations).reverseRangeWithScores("leaderboard:global:artists", 0, 9);
        doThrow(new ArtistNotFoundException("does not exist")).when(catalogApi).getArtistNameById(1L);
        when(catalogApi.getArtistNameById(2L)).thenReturn("artist");
        when(zSetOperations.zCard(anyString())).thenReturn(2L);

        CustomPageResponse<LeaderboardGlobalArtistEntry> result =
                leaderboardService.getTopGlobalArtists(pageable);

        assertThat(result.content().size()).isEqualTo(1);
        assertThat(result.content().get(0).artistName()).isEqualTo("artist");
    }

    @Test
    void removeDeletedArtistFromLeaderboards_DeletesKeysAndRemovesFromGlobalLeaderboard() {
        doReturn(zSetOperations).when(redisTemplate).opsForZSet();

        leaderboardService.removeDeletedArtistFromLeaderboards(new ArtistDeletedEvent(1L));

        verify(redisTemplate).delete("leaderboard:artist:1");
        verify(redisTemplate).delete("leaderboard:artist:1:tracks");
        verify(zSetOperations).remove("leaderboard:global:artists", "1");
    }

    @Test
    void removeDeletedTrackFromLeaderboards_DeletesKeyAndRemovesFromGlobalLeaderboard() {
        doReturn(zSetOperations).when(redisTemplate).opsForZSet();

        leaderboardService.removeDeletedTrackFromLeaderboards(new TrackDeletedEvent(5L));

        verify(redisTemplate).delete("leaderboard:track:5");
        verify(zSetOperations).remove("leaderboard:global:tracks", "5");
    }


}
