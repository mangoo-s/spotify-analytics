package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "user_track_stats")
public class UserTrackStatsEntity {

    @EmbeddedId
    private UserTrackStatsId id;

    private Long playCount;
    private Instant lastListenedAt;

    protected UserTrackStatsEntity() {}

    public UserTrackStatsEntity(UserTrackStatsId id){
        this.id = id;
        this.playCount = 1L;
        this.lastListenedAt = Instant.now();
    }
}
