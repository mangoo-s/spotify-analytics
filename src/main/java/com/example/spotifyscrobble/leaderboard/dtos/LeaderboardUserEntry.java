package com.example.spotifyscrobble.leaderboard.dtos;

public record LeaderboardUserEntry(
        String username,
        String artistName,
        String trackName,
        double score,
        int rank
) {
}
