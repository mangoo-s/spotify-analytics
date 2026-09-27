package com.example.spotifyscrobble.catalog.internalDto;

public record ArtistCacheView(
        long artistId,
        String artistName,
        String artistSpotifyId
) {
}
