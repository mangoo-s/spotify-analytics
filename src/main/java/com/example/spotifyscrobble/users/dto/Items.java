package com.example.spotifyscrobble.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Items(
        @JsonProperty("track") Track item,
        @JsonProperty("played_at") String played_at

) {
}
