package com.example.spotifyscrobble.listening.components;

import com.example.spotifyscrobble.listening.entities.ListenEventEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.Instant;
import java.util.UUID;

public class ListeningHistorySpecifications {

    public static Specification<ListenEventEntity> hasUsername(String username) {
        return (root, query, cb) -> cb.equal(root.get("username"), username);
    }

    public static Specification<ListenEventEntity> hasSpotifyArtistId(String spotifyArtistId) {
        return (root, query, cb) ->
                spotifyArtistId == null ? null : cb.equal(root.get("spotifyArtistId"), spotifyArtistId);
    }

    public static Specification<ListenEventEntity> hasSpotifyTrackId(String spotifyTrackId) {
        return (root, query, cb) ->
                spotifyTrackId == null ? null : cb.equal(root.get("spotifyTrackId"), spotifyTrackId);
    }

    public static Specification<ListenEventEntity> listenedAfter(Instant startDate) {
        return (root, query, cb) ->
                startDate == null ? null : cb.greaterThanOrEqualTo(root.get("playedAt"), startDate);
    }

    public static Specification<ListenEventEntity> listenedBefore(Instant endDate) {
        return (root, query, cb) ->
                endDate == null ? null : cb.lessThanOrEqualTo(root.get("playedAt"), endDate);
    }
}
