package com.example.spotifyscrobble.leaderboard.dtos;

public record LeaderboardGlobalTrackEntry(
        String artistName,
        String trackName,
        double score,
        int rank
) {
}
