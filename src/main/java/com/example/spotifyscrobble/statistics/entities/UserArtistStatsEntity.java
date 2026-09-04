package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_artist_stats")
@Getter
public class UserArtistStatsEntity {
    @Id
    private UserArtistStatsId id;

    private String artistName;

    private long playCount;
    private Instant lastPlayed;

    protected UserArtistStatsEntity() {}

    public UserArtistStatsEntity(UserArtistStatsId id, String artistName){
        this.id = id;
        this.playCount = 1L;
        this.lastPlayed = Instant.now();
        this.artistName = artistName;
    }
}
