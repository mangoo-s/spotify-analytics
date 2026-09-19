package com.example.spotifyscrobble.catalog;

import com.example.spotifyscrobble.catalog.entity.TrackEntity;

public record TrackCreatedEvent(
        long trackId,
        String title,
        String spotifyId

) {
}
