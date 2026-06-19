package com.example.spotifyscrobble.listening;

import com.example.spotifyscrobble.catalog.ArtistEntity;
import com.example.spotifyscrobble.catalog.TrackEntity;

import java.time.Instant;

public record TrackListenedEvent(Long userId, Long artistId,
                                 Long trackId,
                                 Instant playedAt
) {
}
