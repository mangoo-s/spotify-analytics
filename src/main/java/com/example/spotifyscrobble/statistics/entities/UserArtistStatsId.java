package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record UserArtistStatsId(
        java.util.UUID userId,
        Long artistId
) implements Serializable {
}
