package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.*;
import com.example.spotifyscrobble.catalog.TrackEntity;

@Entity
@Table(name = "track_stats")
public class TrackStatsEntity {
    @Id
    private Long trackId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "track_id")
    private TrackEntity track;

    private Long totalPlays;
    private Long listeners;


    protected TrackStatsEntity() {}

    public TrackStatsEntity(TrackEntity track){
        this.totalPlays = 0L;
        this.listeners = 0L;
        this.track = track;

    }
}
