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
    private String spotifyArtistId;

    @NotNull(message = "trackId cannot be null")
    private String spotifyTrackId;

    @NotNull(message = "playedAt cannot be null")
    private Instant playedAt;

    @NotNull(message = "Duration of track cannot be null")
    private long duration;

    protected ListenEventEntity() { }

    public ListenEventEntity(UUID userId, String username, String artistName, String trackName, String spotifyTrackId, String spotifyArtistId, Instant playedAt, long duration) {
        this.userId = userId;
        this.username = username;
        this.trackName = trackName;
        this.artistName = artistName;
        this.spotifyTrackId = spotifyTrackId;
        this.spotifyArtistId = spotifyArtistId;
        this.playedAt = playedAt;
        this.duration = duration;
    }

}
