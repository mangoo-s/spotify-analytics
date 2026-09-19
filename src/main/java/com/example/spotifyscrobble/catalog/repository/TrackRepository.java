package com.example.spotifyscrobble.catalog.repository;

import com.example.spotifyscrobble.catalog.entity.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<TrackEntity, Long> {

    Optional<TrackEntity> findBySpotifyIdAndDeletedAtIsNull(String spotifyId);

    boolean existsBySpotifyIdAndDeletedAtIsNull(String spotifyId);

    boolean existsByTrackIdAndDeletedAtIsNull(long artistId);

    Optional<TrackEntity> findByTrackIdAndDeletedAtIsNull(long id);
}
