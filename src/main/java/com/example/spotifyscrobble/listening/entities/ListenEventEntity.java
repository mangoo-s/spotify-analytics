package com.example.spotifyscrobble.listening.entities;

import com.example.spotifyscrobble.users.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Table(name = "listening_history")
public class ListenEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @NotNull(message = "UserId cannot be null")
    private UUID userId;

    @NotBlank(message = "Username can not be null")
    private String username;

    @NotBlank(message = "Artist name cannot be null")
    private String artistName;

    @NotBlank(message = "Track name cannot be null")
    private String trackName;

    @NotNull(message = "ArtistId cannot be null")
    private Long spotifyArtistId;

    @NotNull(message = "trackId cannot be null")
    private Long spotifyTrackId;

    @NotNull(message = "playedAt cannot be null")
    private Instant playedAt;

    @CreatedDate
    private Instant createdAt;

    protected ListenEventEntity() { }

    public ListenEventEntity(UUID userId, String username, String artistName, String trackName, Long spotifyTrackId, Long spotifyArtistId, Instant playedAt) {
        this.userId = userId;
        this.username = username;
        this.trackName = trackName;
        this.artistName = artistName;
        this.spotifyTrackId = spotifyTrackId;
        this.spotifyArtistId = spotifyArtistId;
        this.playedAt = playedAt;
    }

}
