package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;

@Entity
@Table(name = "user_track_stats")
@Getter
public class UserTrackStatsEntity {

    @Id
    private UserTrackStatsId id;

    private String artistName;
    private String trackName;

    private Long playCount;
    private Instant lastListenedAt;

    protected UserTrackStatsEntity() {}

    public UserTrackStatsEntity(UserTrackStatsId id, String artistName, String trackName){
        this.id = id;
        this.playCount = 1L;
        this.lastListenedAt = Instant.now();
        this.artistName = artistName;
        this.trackName = trackName;
    }
}
