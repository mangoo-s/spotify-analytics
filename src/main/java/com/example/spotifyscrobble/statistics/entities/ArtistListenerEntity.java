package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.UUID;

@Entity
@Table(name = "artist_listeners", uniqueConstraints = {@UniqueConstraint(
        columnNames = {"artist_id", "user_id"}
)})
@Getter
public class ArtistListenerEntity {
    @Id
    @GeneratedValue
    private Long id;

    private UUID userId;
    private Long artistId;

    protected ArtistListenerEntity() {}

    public ArtistListenerEntity(Long artistId, UUID userId){
        this.artistId = artistId;
        this.userId = userId;
    }

}
