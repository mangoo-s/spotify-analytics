package com.example.spotifyscrobble.statistics.repositories;

import com.example.spotifyscrobble.statistics.entities.UserTrackStatsEntity;
import com.example.spotifyscrobble.statistics.entities.UserTrackStatsId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface UserTrackStatsRepository extends JpaRepository<UserTrackStatsEntity, UserTrackStatsId> {
    @Modifying
    @Query("""
    UPDATE UserTrackStatsEntity arts
    SET arts.playCount = arts.playCount + 1
    WHERE arts.id.trackId = :#{#id.trackId}
    AND arts.id.userId = :#{#id.userId}
    """)
    int incrementTotalPlays(UserTrackStatsId id);

    Page<UserTrackStatsEntity> findAllById_UserId(UUID id, Pageable p);
}
