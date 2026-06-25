package com.example.spotifyscrobble.statistics.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import java.time.Instant;

@Entity
public class UserTrackEntity {

    @EmbeddedId
    private UserTrackId id;

    private Long playCount;
    private Instant lastListenedAt;

    protected UserTrackEntity() {}

    public UserTrackEntity(UserTrackId id){
        this.id = id;
        this.playCount = 1L;
        this.lastListenedAt = Instant.now();
    }
}
