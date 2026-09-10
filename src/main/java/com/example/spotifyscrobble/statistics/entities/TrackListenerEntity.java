package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;

import java.util.UUID;
@Entity
@Table(name = "track_listeners", uniqueConstraints = {@UniqueConstraint(
        columnNames = {"trackId", "user_id"}
)})
@Getter
public class TrackListenerEntity {
    @Id
    private Long trackId;

    private UUID userId;

    protected TrackListenerEntity(){}

    public TrackListenerEntity(Long trackId, UUID userId){
        this.trackId = trackId;
        this.userId = userId;
    }
}
