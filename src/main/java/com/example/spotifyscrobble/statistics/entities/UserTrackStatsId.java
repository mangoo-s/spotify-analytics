package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public record UserTrackStatsId(
        Long userId,
        Long trackId
) implements Serializable {
}
