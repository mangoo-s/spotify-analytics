package com.example.spotifyscrobble.users.dto;

import com.example.spotifyscrobble.users.other.CurrentlyPlayingResult;
import com.fasterxml.jackson.annotation.JsonProperty;

public record GetCurrentlyPlayingTrackResponse(
        @JsonProperty("progress_ms") int timestamp,
        @JsonProperty("item") Item item

) {

    public static CurrentlyPlayingResult playing(GetCurrentlyPlayingTrackResponse t) {
        return new CurrentlyPlayingResult(t, CurrentlyPlayingResult.Status.PLAYING);
    }
    public static CurrentlyPlayingResult nothingPlaying() {
        return new CurrentlyPlayingResult(null, CurrentlyPlayingResult.Status.NOTHING_PLAYING);
    }
    public static CurrentlyPlayingResult authFailed() {
        return new CurrentlyPlayingResult(null, CurrentlyPlayingResult.Status.AUTH_FAILED);
    }
    public static CurrentlyPlayingResult transientFailure() {
        return new CurrentlyPlayingResult(null, CurrentlyPlayingResult.Status.TRANSIENT_FAILURE);
    }
}
