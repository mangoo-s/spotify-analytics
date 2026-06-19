package com.example.spotifyscrobble.statistics.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.spotifyscrobble.statistics.entity.TrackStatsEntity;

public interface TrackStatsRepository extends JpaRepository<TrackStatsEntity, Long> {
    @Modifying
    @Query("""
    UPDATE TrackStatsEntity ts
    SET ts.totalPlays = ts.totalPlays + 1
    WHERE ts.trackId = :trackId
    """)
    int incrementTrackPlays(@Param("trackId") Long trackId);
}
