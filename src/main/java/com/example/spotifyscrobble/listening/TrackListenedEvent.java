package com.example.spotifyscrobble.listening;

import java.time.Instant;
import java.util.UUID;

public record TrackListenedEvent(
        UUID userId,
        String username,//No idea if this is even necessary
        String spotifyTrackId,
        String spotifyArtistId,
        Instant playedAt,
        String artistName,
        String trackName
) {
}
