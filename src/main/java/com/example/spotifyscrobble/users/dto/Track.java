package com.example.spotifyscrobble.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Track(
        @JsonProperty("name") String name,
        @JsonProperty("id") String spotifyId,
        @JsonProperty("artists") List<Artists> artists,
        @JsonProperty("duration_ms") long duration
) {
}
