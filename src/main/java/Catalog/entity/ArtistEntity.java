package Catalog.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Entity
@Table(name = "artists")
@Getter
public class ArtistEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long artistId;

    @NotBlank
    private String name;

    @NotNull
    @Column(unique = true)
    private Long spotifyId;

    protected ArtistEntity() {}

    public ArtistEntity(String name, Long spotifyId){
        this.name = name;
        this.spotifyId = spotifyId;
    }

}
