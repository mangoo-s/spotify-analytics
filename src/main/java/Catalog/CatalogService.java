package Catalog;

import Catalog.entity.ArtistEntity;
import Catalog.entity.TrackEntity;
import Catalog.repository.ArtistRepository;
import Catalog.repository.TrackRepository;
import listening.TrackListenedRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CatalogService {
    private final ArtistRepository artistRepo;
    private final TrackRepository trackRepo;

    public CatalogService(ArtistRepository artistRepo, TrackRepository trackRepo) {
        this.artistRepo = artistRepo;
        this.trackRepo = trackRepo;
    }

    public CatalogEntriesExistResponse ensureCatalogEntriesExist(TrackListenedRequest eventRequest){
        ArtistEntity newArtist = artistRepo.findBySpotifyId(eventRequest.spotifyArtistId()).orElseGet(()-> {
            ArtistEntity artist = new ArtistEntity(eventRequest.artistName(), eventRequest.spotifyArtistId());
            return artistRepo.save(artist);
        });


        TrackEntity trackEntity = trackRepo.findBySpotifyId(eventRequest.spotifyTrackId()).orElseGet(()-> {
            TrackEntity newTrack = new TrackEntity(eventRequest.spotifyTrackId(),newArtist, eventRequest.trackName(), 0L);
            return trackRepo.save(newTrack);
        });
        return new CatalogEntriesExistResponse(newArtist, trackEntity);
    }
}
