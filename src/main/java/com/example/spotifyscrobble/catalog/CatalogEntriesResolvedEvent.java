package com.example.spotifyscrobble.catalog;

public record CatalogEntriesResolvedEvent(
        Long userId,
        Long artistId,
        Long trackId,
        String trackName,
        String artistName
) {
}
