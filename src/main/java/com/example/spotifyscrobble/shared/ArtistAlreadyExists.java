package com.example.spotifyscrobble.shared;

public class ArtistAlreadyExists extends RuntimeException {
    public ArtistAlreadyExists(String message) {
        super(message);
    }
}
