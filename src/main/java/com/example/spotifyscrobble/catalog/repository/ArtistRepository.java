package com.example.spotifyscrobble.catalog.repository;

import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    Optional<ArtistEntity> findBySpotifyId(String spotifyId);

    boolean existsBySpotifyId(String spotifyId);
}
