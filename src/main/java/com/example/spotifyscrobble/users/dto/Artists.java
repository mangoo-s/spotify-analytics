package com.example.spotifyscrobble.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Artists(
        @JsonProperty("id") String spotifyId,
        @JsonProperty("name") String name
) {
}
