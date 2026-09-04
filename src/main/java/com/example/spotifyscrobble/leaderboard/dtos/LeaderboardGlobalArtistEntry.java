package com.example.spotifyscrobble.leaderboard.dtos;

public record LeaderboardGlobalArtistEntry(
        String artistName,
        double score,
        int rank
) {
}
