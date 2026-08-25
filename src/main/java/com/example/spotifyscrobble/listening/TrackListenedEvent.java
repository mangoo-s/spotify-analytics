package com.example.spotifyscrobble.listening;

import java.time.Instant;
import java.util.UUID;

public record TrackListenedEvent(
        UUID userId,
        String username,
        String spotifyTrackId,
        String spotifyArtistId,
        Instant playedAt,
        String artistName,
        String trackName,
        long duration
) {
}
