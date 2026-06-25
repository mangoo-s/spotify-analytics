package com.example.spotifyscrobble.statistics.repositories;

import com.example.spotifyscrobble.statistics.entity.UserTrackEntity;
import com.example.spotifyscrobble.statistics.entity.UserTrackId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserTrackStatsRepository extends JpaRepository<UserTrackEntity, UserTrackId> {
}
