package com.example.spotifyscrobble.shared;

import java.time.Instant;

public record ErrorResponse(String message,
                            Instant time) {
}
