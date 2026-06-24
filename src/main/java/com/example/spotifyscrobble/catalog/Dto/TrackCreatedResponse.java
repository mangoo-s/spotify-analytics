package com.example.spotifyscrobble.catalog.Dto;

public record TrackCreatedResponse(
        Long spotifyId,
        String title,
        String artist,
        Long duration
) {
}
