package com.example.spotifyscrobble.leaderboard.service;

import com.example.spotifyscrobble.catalog.CatalogApi;
import com.example.spotifyscrobble.catalog.GetArtistAndTrackbyTrackIdDto;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardArtistEntry;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardGlobalArtistEntry;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardGlobalTrackEntry;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardUserEntry;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.users.UsersApi;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class LeaderboardService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final UsersApi usersApi;
    private final CatalogApi catalogApi;

    public LeaderboardService(RedisTemplate<String, Object> redisTemplate, UsersApi usersApi, CatalogApi catalogApi) {
        this.redisTemplate = redisTemplate;
        this.usersApi = usersApi;
        this.catalogApi = catalogApi;
    }

    private String artistKey(long artistId){
        return "leaderboard:artist:"+artistId;
    }

    private String trackKey(long trackId){
        return "leaderboard:track:"+trackId;
    }

    private String artistTopTracksKey(long artistId){
        return "leaderboard:artist:"+ artistId +":tracks";
    }

    public void recordPlay(long artistId, long trackId, UUID userId){
        RedisSerializer<String> serializer = new StringRedisSerializer();
        long startTime = System.nanoTime();
        redisTemplate.executePipelined((RedisCallback<?>) connection -> {
            byte[] artistKeyArtistId = serializer.serialize(artistKey(artistId));
            byte[] valueArtistId = serializer.serialize(String.valueOf(artistId));
            byte[] trackKeyTrackId = serializer.serialize(trackKey(trackId));
            byte[] valueTrackId = serializer.serialize(String.valueOf(trackId));
            byte[] artistTopTrackKey = serializer.serialize(artistTopTracksKey(artistId));

            byte[] serializedUserId = serializer.serialize(userId.toString());

            byte[] globalArtistKey = serializer.serialize("leaderboard:global:artists");
            byte[] globalTrackKey = serializer.serialize("leaderboard:global:tracks");

            connection.zSetCommands().zIncrBy(artistKeyArtistId, 1, serializedUserId);
            connection.zSetCommands().zIncrBy(trackKeyTrackId, 1, serializedUserId);
            connection.zSetCommands().zIncrBy(artistTopTrackKey, 1, valueTrackId);
            connection.zSetCommands().zIncrBy(globalArtistKey, 1, valueArtistId);
            connection.zSetCommands().zIncrBy(globalTrackKey, 1, valueTrackId);

            return null;
        });
        double elapsedTime = (double) (System.nanoTime() - startTime) / 1e+6;
        log.info("Redis pipelining in LeaderboardService completed in {} ms.", String.format("%.2f", elapsedTime));


    }

    public CustomPageResponse<LeaderboardArtistEntry> getTopArtistListeners(long artistId, Pageable pageable){
        int start = (int) pageable.getOffset();
        int end = start + pageable.getPageSize();

        Set<ZSetOperations.@NonNull TypedTuple<Object>> results = redisTemplate.opsForZSet().reverseRangeWithScores(artistKey(artistId), start, end-1);
        List<LeaderboardArtistEntry> leaderboardToList = new ArrayList<>();

        if (results != null && !results.isEmpty()) {
            String name  = catalogApi.getArtistNameById(artistId);
            int rank = start+1;
            for (ZSetOperations.TypedTuple<Object> res : results) {
                Object value = res.getValue();
                Double score = res.getScore();

                if (value == null || score == null) {
                    log.warn("Value or score is null in LeaderboardService getTopArtistListeners");
                    continue;
                }

                leaderboardToList.add(
                        new LeaderboardArtistEntry(
                                usersApi.getUsernameByUserId(UUID.fromString(value.toString())),
                                name,
                                score,
                                rank
                        )
                );
                rank++;
            }
        }

        Long totalElements = redisTemplate.opsForZSet().zCard(trackKey(artistId));
        totalElements = totalElements == null ? 0L : totalElements;

        Page<LeaderboardArtistEntry> page = new PageImpl<>(leaderboardToList, pageable, totalElements);
        return new CustomPageResponse<>(page);
    }

    public CustomPageResponse<LeaderboardUserEntry> getTopTrackListeners(long trackId, Pageable pageable){
        int start = (int) pageable.getOffset();
        int end = start + pageable.getPageSize();

        Set<ZSetOperations.@NonNull TypedTuple<Object>> results = redisTemplate.opsForZSet().reverseRangeWithScores(trackKey(trackId), start, end-1);
        List<LeaderboardUserEntry> leaderboardToList = new ArrayList<>();

        if (results != null && !results.isEmpty()) {
            GetArtistAndTrackbyTrackIdDto getArtistAndTrackbyTrackIdDto = catalogApi.getArtistAndTrackByTrackId(trackId);
            int rank = start+1;
            for (ZSetOperations.TypedTuple<Object> res : results) {
                Object value = res.getValue();
                Double score = res.getScore();

                if (value == null || score == null) {
                    log.warn("Value or score is null in LeaderboardService getTopTrackListeners");
                    continue;
                }

                leaderboardToList.add(
                        new LeaderboardUserEntry(
                                usersApi.getUsernameByUserId(UUID.fromString(value.toString())),
                                getArtistAndTrackbyTrackIdDto.artistName(),
                                getArtistAndTrackbyTrackIdDto.trackName(),
                                score,
                                rank
                        )
                );
                rank++;
            }
        }

        Long totalElements = redisTemplate.opsForZSet().zCard(trackKey(trackId));
        totalElements = totalElements == null ? 0L : totalElements;

        Page<LeaderboardUserEntry> page = new PageImpl<>(leaderboardToList, pageable, totalElements);
        return new CustomPageResponse<>(page);
    }

    public CustomPageResponse<LeaderboardGlobalTrackEntry> getTopGlobalTracks(Pageable page){
        int start = (int) page.getOffset();
        int end = start + page.getPageSize();

        Set<ZSetOperations.@NonNull TypedTuple<Object>> results = redisTemplate.opsForZSet().reverseRangeWithScores("leaderboard:global:tracks", start, end-1);
        List<LeaderboardGlobalTrackEntry> leaderboardToList = new ArrayList<>();

        if (results != null && !results.isEmpty()) {
            int rank = start+1;
            for (ZSetOperations.TypedTuple<Object> res : results) {
                Object value = res.getValue();
                Double score = res.getScore();


                if (value == null || score == null) {
                    log.warn("Value or score is null in LeaderboardService getTopGlobalTracks");
                    continue;
                }
                GetArtistAndTrackbyTrackIdDto getArtistAndTrackbyTrackIdDto = catalogApi.getArtistAndTrackByTrackId(Long.parseLong((String) value));
                leaderboardToList.add(
                        new LeaderboardGlobalTrackEntry(
                                getArtistAndTrackbyTrackIdDto.artistName(),
                                getArtistAndTrackbyTrackIdDto.trackName(),
                                score,
                                rank
                        )
                );
                rank++;
            }
        }

        Long totalElements = redisTemplate.opsForZSet().zCard("leaderboard:global:tracks");
        totalElements = totalElements == null ? 0L : totalElements;

        Page<LeaderboardGlobalTrackEntry> p = new PageImpl<>(leaderboardToList, page, totalElements);
        return new CustomPageResponse<>(p);
    }

    public CustomPageResponse<LeaderboardGlobalArtistEntry> getTopGlobalArtists(Pageable page){
        int start = (int) page.getOffset();
        int end = start + page.getPageSize();

        Set<ZSetOperations.@NonNull TypedTuple<Object>> results = redisTemplate.opsForZSet().reverseRangeWithScores("leaderboard:global:artists", start, end-1);
        List<LeaderboardGlobalArtistEntry> leaderboardToList = new ArrayList<>();

        if (results != null && !results.isEmpty()) {
            int rank = start+1;
            for (ZSetOperations.TypedTuple<Object> res : results) {
                Object value = res.getValue();
                Double score = res.getScore();


                if (value == null || score == null) {
                    log.warn("Value or score is null in LeaderboardService getTopGlobalArtists");
                    continue;
                }
                String artistName = catalogApi.getArtistNameById(Long.parseLong((String) value));
                leaderboardToList.add(
                        new LeaderboardGlobalArtistEntry(
                                artistName,
                                score,
                                rank
                        )
                );
                rank++;
            }
        }

        Long totalElements = redisTemplate.opsForZSet().zCard("leaderboard:global:tracks");
        totalElements = totalElements == null ? 0L : totalElements;

        Page<LeaderboardGlobalArtistEntry> p = new PageImpl<>(leaderboardToList, page, totalElements);
        return new CustomPageResponse<>(p);
    }


}
