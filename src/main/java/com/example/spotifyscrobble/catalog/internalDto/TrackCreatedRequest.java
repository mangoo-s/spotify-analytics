package com.example.spotifyscrobble.catalog.internalDto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TrackCreatedRequest(
        @NotBlank String title,
        @NotNull Long spotifyId,
        @NotNull Long artistSpotifyId,
        @NotNull Long duration

) {
}
