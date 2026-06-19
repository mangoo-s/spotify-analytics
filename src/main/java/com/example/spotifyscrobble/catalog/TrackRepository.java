package com.example.spotifyscrobble.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TrackRepository extends JpaRepository<TrackEntity, Long> {
    Optional<TrackEntity> findBySpotifyId(Long spotifyId);
}
