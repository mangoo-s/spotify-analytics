package com.example.spotifyscrobble.statistics.repositories;

import com.example.spotifyscrobble.statistics.entity.UserArtistStatsEntity;
import com.example.spotifyscrobble.statistics.entity.UserArtistStatsId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserArtistStatsRepository extends JpaRepository<UserArtistStatsEntity, UserArtistStatsId> {
}
