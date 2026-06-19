package com.example.spotifyscrobble.statistics.entity;

import jakarta.persistence.*;
import com.example.spotifyscrobble.catalog.ArtistEntity;

@Entity
@Table(name = "artist_stats")
public class ArtistStatsEntity {
    @Id
    private Long artistId;

    @OneToOne(optional = false)
    @MapsId
    @JoinColumn(name = "artist_id", nullable = false)
    private ArtistEntity artist;

    private Long listeners;
    private Long totalPlays;


    protected ArtistStatsEntity() {}

    public ArtistStatsEntity(ArtistEntity artist){
        this.totalPlays = 0L;
        this.listeners = 0L;
        this.artist = artist;

    }
}
