package com.example.spotifyscrobble.shared;

public class ArtistAlreadyExistsInStatsException extends RuntimeException {
    public ArtistAlreadyExistsInStatsException(String message) {
        super(message);
    }
}
