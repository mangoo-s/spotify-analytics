package com.example.spotifyscrobble.users.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
public class SpotifyConnectionEntity {
    @Id
    private UUID userId;

    @Setter
    private Instant expiresIn;

    @Setter
    private String accessToken;

    private String refreshToken;

    @OneToOne
    @MapsId
    @JoinColumn(name = "userId")
    private UserEntity user;

    protected SpotifyConnectionEntity(){}

    public SpotifyConnectionEntity(UserEntity user, int expiresIn, String accessToken, String refreshToken){
        this.user = user;
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.expiresIn = Instant.now().plusSeconds(expiresIn);
    }
}
