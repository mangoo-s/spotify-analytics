package com.example.spotifyscrobble.users.exceptions;

public class SpotifyReauthRequiredException extends SpotifyTokenRefreshException {
    public SpotifyReauthRequiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
