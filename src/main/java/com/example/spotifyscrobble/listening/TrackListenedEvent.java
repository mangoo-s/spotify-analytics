package com.example.spotifyscrobble.listening;

import java.time.Instant;

public record TrackListenedEvent(
        Long userId,
        Long spotifyTrackId,
        Long spotifyArtistId,
        Instant playedAt,
        String artistName,
        String trackName
) {
}
