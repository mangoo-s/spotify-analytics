package com.example.spotifyscrobble.statistics.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.spotifyscrobble.statistics.entity.ArtistStatsEntity;

public interface ArtistStatsRepository extends JpaRepository<ArtistStatsEntity, Long> {
    @Modifying
    @Query("""
    UPDATE ArtistStatsEntity arts
    SET arts.totalPlays = arts.totalPlays + 1
    WHERE arts.artistId= :artistId
    """)
    int incrementTotalPlays(@Param("artistId") Long artistId);

    @Modifying
    @Query("""
    UPDATE ArtistStatsEntity arts
    SET arts.listeners = arts.listeners + 1
    WHERE arts.artistId= :artistId
    """)
    int incrementListeners(@Param("artistId") Long artistId);

}
