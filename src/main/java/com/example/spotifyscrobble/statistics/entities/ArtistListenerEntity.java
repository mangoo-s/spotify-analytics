package com.example.spotifyscrobble.statistics.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "artist_listeners", uniqueConstraints = {@UniqueConstraint(
        columnNames = {"artist_id", "user_id"}
)})
public class ArtistListenerEntity {
    @Id
    @GeneratedValue
    private Long id;

    private Long userId;
    private Long artistId;

    protected ArtistListenerEntity() {}

    public ArtistListenerEntity(Long artistId, Long userId){
        this.artistId = artistId;
        this.userId = userId;
    }

}
