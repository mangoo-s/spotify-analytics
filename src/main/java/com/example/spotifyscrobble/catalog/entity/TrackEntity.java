package com.example.spotifyscrobble.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tracks")
@Getter
public class TrackEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long trackId;

    @NotNull
    @Column(unique = true)
    @Setter
    private String spotifyId;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;

    @NotBlank
    @Setter
    private String title;

    @NotNull
    @Setter
    private Long duration;

    protected TrackEntity() {}

    public TrackEntity(String spotifyId, ArtistEntity artist, String title, Long duration){
        this.spotifyId = spotifyId;
        this.artist = artist;
        this.title = title;
        this.duration = duration;
    }

}
