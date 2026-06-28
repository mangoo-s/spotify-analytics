package com.example.spotifyscrobble.statistics.entities;

import com.example.spotifyscrobble.catalog.ArtistEntity;
import com.example.spotifyscrobble.users.UserEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "artist_listeners", uniqueConstraints = {@UniqueConstraint(
        columnNames = {"artist_id", "user_id"}
)})
public class ArtistListenerEntity {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "artist_id")
    private ArtistEntity artist;

    protected ArtistListenerEntity() {}

    public ArtistListenerEntity(ArtistEntity artist, UserEntity user){
        this.artist = artist;
        this.user = user;
    }

}
