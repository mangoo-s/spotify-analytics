package com.example.spotifyscrobble.statistics;

public record UserTopArtistResponse(
        String artistName,
        Long totalPlays
) {
}
