package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "artist_stats")
@Getter
public class ArtistStatsEntity {
    @Id
    private Long artistId;

    private String name;

    private Long listeners;
    private Long totalPlays;


    protected ArtistStatsEntity() {}

    public ArtistStatsEntity(Long artistId, String name){
        this.totalPlays = 1L;
        this.listeners = 0L;
        this.artistId = artistId;
        this.name = name;

    }
}
