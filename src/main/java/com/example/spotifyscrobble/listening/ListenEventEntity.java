package com.example.spotifyscrobble.listening;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Getter
@Entity
public class ListenEventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @NotNull(message = "UserId cannot be null")
    private Long userId;

    @NotNull(message = "ArtistId cannot be null")
    private Long spotifyArtistId;

    @NotNull(message = "trackId cannot be null")
    private Long spotifyTrackId;

    @NotNull(message = "playedAt cannot be null")
    private Instant playedAt;

    @CreatedDate
    private Instant createdAt;

    protected ListenEventEntity() { }

    public ListenEventEntity(Long userId, Long spotifyTrackId, Long spotifyArtistId, Instant playedAt) {
        this.userId = userId;
        this.spotifyTrackId = spotifyTrackId;
        this.spotifyArtistId = spotifyArtistId;
        this.playedAt = playedAt;
    }

}
