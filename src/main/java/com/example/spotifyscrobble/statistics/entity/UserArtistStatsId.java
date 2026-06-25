package com.example.spotifyscrobble.statistics.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public record UserArtistStatsId(
        Long userId,
        Long artistId
) implements Serializable {
}
