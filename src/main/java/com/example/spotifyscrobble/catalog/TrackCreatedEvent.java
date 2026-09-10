package com.example.spotifyscrobble.catalog;

import com.example.spotifyscrobble.catalog.entity.TrackEntity;

public record TrackCreatedEvent(
        TrackEntity track
) {
}
