package com.example.spotifyscrobble.statistics;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ArtistListenerRepository extends JpaRepository<ArtistListenerEntity, Long> {
    Optional<ArtistListenerEntity> findByArtist_ArtistIdAndUser_UserId(Long artist_artistId, Long user_userId);

    boolean existsByArtist_ArtistIdAndUser_UserId(Long artist_artistId, Long user_UserId);

    
}
