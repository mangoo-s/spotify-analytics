package com.example.spotifyscrobble.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Item(
        @JsonProperty("name") String name,
        @JsonProperty("id") String spotifyId,
        @JsonProperty("artists") List<Artists> artists
) {
}
