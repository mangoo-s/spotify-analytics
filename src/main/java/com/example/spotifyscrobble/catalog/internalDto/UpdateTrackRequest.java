package com.example.spotifyscrobble.catalog.internalDto;

public record UpdateTrackRequest(
        String name,
        String trackSpotifyId,
        Long duration

) {
}
