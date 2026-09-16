package com.example.spotifyscrobble.catalog.internalDto;

public record GetTrackResponse(
        String trackName,
        String artistName,
        String trackSpotifyId,
        String artistSpotifyId,
        long length
) {
}
