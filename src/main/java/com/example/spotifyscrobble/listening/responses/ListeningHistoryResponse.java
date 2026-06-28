package com.example.spotifyscrobble.listening.responses;

import java.time.Instant;


public record ListeningHistoryResponse(
        String artistName,
        String trackName,
        Long spotifyArtistId,
        Long spotifyTrackId,
        Instant playedAt
) {
}
