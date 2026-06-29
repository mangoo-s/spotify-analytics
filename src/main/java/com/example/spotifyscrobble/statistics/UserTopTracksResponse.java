package com.example.spotifyscrobble.statistics;

public record UserTopTracksResponse(
        String title,
        String artist,
        Long totalPlays
) {
}
