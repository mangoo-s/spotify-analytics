package com.example.spotifyscrobble.users.other;

import com.example.spotifyscrobble.users.dto.GetRecentlyPlayedTracksResponse;
import com.example.spotifyscrobble.users.dto.Items;
import net.minidev.json.annotate.JsonIgnore;

import java.util.List;

public record CurrentlyPlayingResult(
        GetRecentlyPlayedTracksResponse response,
        @JsonIgnore Status status
) {
    public enum Status { OK, AUTH_FAILED, TRANSIENT_FAILURE }

    public static CurrentlyPlayingResult ok(GetRecentlyPlayedTracksResponse t){
        return new CurrentlyPlayingResult(t, Status.OK);
    }
    public static CurrentlyPlayingResult authFailed() {
        return new CurrentlyPlayingResult(null, Status.AUTH_FAILED);
    }
    public static CurrentlyPlayingResult transientFailure() {
        return new CurrentlyPlayingResult(null, Status.TRANSIENT_FAILURE);
    }

    public static CurrentlyPlayingResult none() {
        return new CurrentlyPlayingResult(null, Status.TRANSIENT_FAILURE);
    }
}
