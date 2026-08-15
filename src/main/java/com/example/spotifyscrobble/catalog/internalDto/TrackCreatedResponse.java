package com.example.spotifyscrobble.catalog.internalDto;

public record TrackCreatedResponse(
        String spotifyId,
        String title,
        String artist,
        Long duration
) {
}
