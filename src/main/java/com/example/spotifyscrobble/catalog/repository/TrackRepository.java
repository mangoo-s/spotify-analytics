package com.example.spotifyscrobble.catalog.repository;

import com.example.spotifyscrobble.catalog.entity.TrackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<TrackEntity, Long> {
    Optional<TrackEntity> findBySpotifyId(String spotifyId);

    boolean existsBySpotifyId(String spotifyId);
}
