package com.example.spotifyscrobble.users.other;

import com.example.spotifyscrobble.users.dto.GetCurrentlyPlayingTrackResponse;

public record CurrentlyPlayingResult(
        GetCurrentlyPlayingTrackResponse result,
        Status status
) {
    public enum Status { PLAYING, NOTHING_PLAYING, AUTH_FAILED, TRANSIENT_FAILURE }

    public static CurrentlyPlayingResult playing(GetCurrentlyPlayingTrackResponse t) {
        return new CurrentlyPlayingResult(t, Status.PLAYING);
    }
    public static CurrentlyPlayingResult nothingPlaying() {
        return new CurrentlyPlayingResult(null, Status.NOTHING_PLAYING);
    }
    public static CurrentlyPlayingResult authFailed() {
        return new CurrentlyPlayingResult(null, Status.AUTH_FAILED);
    }
    public static CurrentlyPlayingResult transientFailure() {
        return new CurrentlyPlayingResult(null, Status.TRANSIENT_FAILURE);
    }
}
