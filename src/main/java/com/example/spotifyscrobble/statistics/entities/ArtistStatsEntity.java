package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "artist_stats")
public class ArtistStatsEntity {
    @Id
    private Long artistId;

    private Long listeners;
    private Long totalPlays;


    protected ArtistStatsEntity() {}

    public ArtistStatsEntity(Long artistId){
        this.totalPlays = 1L;
        this.listeners = 0L;
        this.artistId = artistId;

    }
}
