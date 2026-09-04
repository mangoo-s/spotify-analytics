package com.example.spotifyscrobble.catalog;

import java.util.UUID;

public record CatalogEntriesResolvedEvent(
        UUID userId,
        Long artistId,
        Long trackId,
        String trackName,
        String artistName
) {
}
