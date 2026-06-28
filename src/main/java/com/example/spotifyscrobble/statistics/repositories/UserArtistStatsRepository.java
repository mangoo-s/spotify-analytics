package com.example.spotifyscrobble.statistics.repositories;

import com.example.spotifyscrobble.statistics.entities.UserArtistStatsEntity;
import com.example.spotifyscrobble.statistics.entities.UserArtistStatsId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface UserArtistStatsRepository extends JpaRepository<UserArtistStatsEntity, UserArtistStatsId> {
    @Modifying
    @Query("""
    UPDATE UserArtistStatsEntity arts
    SET arts.playCount = arts.playCount + 1
    WHERE arts.id.artistId = :#{#id.artistId}
    AND arts.id.userId = :#{#id.userId}
    """)
    int incrementTotalPlays(UserArtistStatsId id);

}
