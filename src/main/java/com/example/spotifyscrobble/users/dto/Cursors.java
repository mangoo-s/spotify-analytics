package com.example.spotifyscrobble.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Cursors(
        @JsonProperty("after") String after
) {
}
