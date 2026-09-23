package com.example.spotifyscrobble.catalog.internalDto;

public record TrackCacheView(
        long trackId,
        String trackName,
        String trackSpotifyId,
        Long duration,
        String artistName,
        String artistSpotifyId,
        long artistId
) {
}
