package com.example.spotifyscrobble.statistics.entity;

import java.io.Serializable;

public record UserTrackId(
        Long userId,
        Long trackId
) implements Serializable {
}
