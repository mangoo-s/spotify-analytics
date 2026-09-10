package com.example.spotifyscrobble.shared;

public class TrackStatAlreadyExists extends RuntimeException {
    public TrackStatAlreadyExists(String message) {
        super(message);
    }
}
