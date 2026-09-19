package com.example.spotifyscrobble.catalog;

import com.example.spotifyscrobble.catalog.entity.ArtistEntity;

public record ArtistCreatedEvent(
        long artistId,
        String artistName,
        String artistSpotifyId
) {
}
