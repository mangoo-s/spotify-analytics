package com.example.spotifyscrobble.catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Entity
@Table(name = "artists")
@Getter
public class ArtistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artistId;

    @NotBlank
    @Setter
    private String name;

    @NotNull
    @Column(unique = true)
    @Setter
    private String spotifyId;

    protected ArtistEntity() {}

    public ArtistEntity(String name, String spotifyId){
        this.name = name;
        this.spotifyId = spotifyId;
    }

    @Override
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(!(obj instanceof ArtistEntity artist)) return false;
        return (Objects.equals(artist.getArtistId(), this.artistId) && artist.getSpotifyId().equals(this.spotifyId));
    }

}
