package com.example.spotifyscrobble.catalog.internalDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtistCreatedRequest(
        @NotBlank(message = "Artist name cannot be blank.") String name,
        @NotNull(message = "Spotify ID cannot be null.") String spotifyId
) {
}