package com.example.spotifyscrobble.users.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
public class SpotifyConnectionEntity {
    @Id
    private UUID userId;

    private Instant expiresIn;

    private String accessToken;

    private String refreshToken;

    protected SpotifyConnectionEntity(){}

    public SpotifyConnectionEntity(UUID userId, int expiresIn, String accessToken, String refreshToken){
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiresIn = Instant.now().plusSeconds(expiresIn);
    }
}
