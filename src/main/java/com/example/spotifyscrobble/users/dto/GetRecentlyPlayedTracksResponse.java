package com.example.spotifyscrobble.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record GetRecentlyPlayedTracksResponse(
        @JsonProperty("items") List<Items> items,
        @JsonProperty("cursors") Cursors cursor
) {
}
