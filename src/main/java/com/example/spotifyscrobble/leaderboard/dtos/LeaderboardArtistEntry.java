package com.example.spotifyscrobble.leaderboard.dtos;

public record LeaderboardArtistEntry(
        String artistName,
        String username,
        double score,
        int rank
) {
}
