package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "track_stats")
public class TrackStatsEntity {
    @Id
    private Long trackId;

    private String title;

    private Long totalPlays;
    private Long listeners;


    protected TrackStatsEntity() {}

    public TrackStatsEntity(Long trackId, String title){
        this.totalPlays = 0L;
        this.listeners = 0L;
        this.trackId = trackId;
        this.title = title;

    }
}
