package com.example.spotifyscrobble.users.dto;

public record SpotifyAccessTokenRequest(
        String grant_type,
        String code,
        String redirect_uri
) {
}
