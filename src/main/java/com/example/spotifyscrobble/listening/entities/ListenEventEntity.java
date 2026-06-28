package com.example.spotifyscrobble.listening.entities;

import com.example.spotifyscrobble.users.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Getter
@Entity
@Table(name = "listening_history")
public class ListenEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

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

    public ListenEventEntity(UserEntity user, String artistName, String trackName, Long spotifyTrackId, Long spotifyArtistId, Instant playedAt) {
        this.user = user;
        this.trackName = trackName;
        this.artistName = artistName;
        this.spotifyTrackId = spotifyTrackId;
        this.spotifyArtistId = spotifyArtistId;
        this.playedAt = playedAt;
    }

}
