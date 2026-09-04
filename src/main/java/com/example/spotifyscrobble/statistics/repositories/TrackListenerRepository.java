package com.example.spotifyscrobble.statistics.repositories;

import com.example.spotifyscrobble.statistics.entities.TrackListenerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TrackListenerRepository extends JpaRepository<TrackListenerEntity, Long> {
    boolean existsByTrackIdAndUserId(Long trackId, UUID userId);
}
