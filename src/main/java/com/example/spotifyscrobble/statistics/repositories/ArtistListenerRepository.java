package com.example.spotifyscrobble.statistics.repositories;

import com.example.spotifyscrobble.statistics.entities.ArtistListenerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ArtistListenerRepository extends JpaRepository<ArtistListenerEntity, Long> {
    Optional<ArtistListenerEntity> findByArtistIdAndUserId(Long artist_artistId, Long userId);

    boolean existsByArtistIdAndUserId(Long artistId, Long UserId);

    
}
