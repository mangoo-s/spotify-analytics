package com.example.spotifyscrobble.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "tracks")
@Getter
public class TrackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trackId;

    @NotNull
    @Column(unique = true)
    private Long spotifyId;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;

    @NotBlank
    private String title;

    @NotNull
    private Long duration;

    protected TrackEntity() {}

    public TrackEntity(Long spotifyId, ArtistEntity artist, String title, Long duration){
        this.spotifyId = spotifyId;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
    }

}
