package com.example.spotifyscrobble.catalog.internalDto;

public record TrackCreatedResponse(
        Long spotifyId,
        String title,
        String artist,
        Long duration
) {
}
