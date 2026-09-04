package com.example.spotifyscrobble.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record SpotifyAccessTokenResponse(
        @JsonProperty("access_token") String access_token,
        @JsonProperty("refresh_token") String refresh_token,
        @JsonProperty("expires_in") int expiresIn
) {
}
