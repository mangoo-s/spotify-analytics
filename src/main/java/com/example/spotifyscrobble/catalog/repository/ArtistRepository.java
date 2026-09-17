package com.example.spotifyscrobble.catalog.repository;

import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;


public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    Optional<ArtistEntity> findBySpotifyIdAndDeletedAtIsNull(String spotifyId);

    Optional<ArtistEntity> findByArtistIdAndDeletedAtIsNull(long artistId);

    boolean existsBySpotifyIdAndDeletedAtIsNull(String spotifyId);

    boolean existsByArtistIdAndDeletedAtIsNull(long artistId);


}
