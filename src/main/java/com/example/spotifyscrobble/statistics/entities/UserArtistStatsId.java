package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record UserArtistStatsId(
        Long userId,
        Long artistId
) implements Serializable {
}
