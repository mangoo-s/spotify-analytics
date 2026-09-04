package com.example.spotifyscrobble.users;

import java.time.Instant;
import java.util.UUID;

public record GetUserByIdResponse(
        UUID id,
        Instant createdAt
) {
}
