package com.example.spotifyscrobble.statistics.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.time.Instant;

@Entity
public class UserArtistStatsEntity {
    @EmbeddedId
    private UserArtistStatsId id;

    private long playCount;
    private Instant lastPlayed;

    protected UserArtistStatsEntity() {}

    public UserArtistStatsEntity(UserArtistStatsId id){
        this.id = id;
        this.playCount = 1L;
        this.lastPlayed = Instant.now();
    }
}
