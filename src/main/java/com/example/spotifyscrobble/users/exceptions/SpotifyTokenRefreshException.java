package com.example.spotifyscrobble.users.exceptions;

public class SpotifyTokenRefreshException extends RuntimeException {
    public SpotifyTokenRefreshException(String message, Throwable cause) {
        super(message, cause);
    }

    public SpotifyTokenRefreshException(String message) {
        super(message);
    }
}
