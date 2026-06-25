package com.example.spotifyscrobble.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ArtistRepository extends JpaRepository<ArtistEntity, Long> {
    Optional<ArtistEntity> findBySpotifyId(Long spotifyId);

    boolean existsBySpotifyId(Long spotifyId);
}
