package com.example.spotifyscrobble.catalog;

public record CatalogEntriesResolvedEvent(
        Long userId,
        ArtistEntity artist,
        TrackEntity track
) {
}
