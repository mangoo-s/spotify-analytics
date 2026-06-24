package com.example.spotifyscrobble.catalog;

import com.example.spotifyscrobble.catalog.Dto.ArtistCreatedRequest;
import com.example.spotifyscrobble.catalog.Dto.ArtistCreatedResponse;
import com.example.spotifyscrobble.catalog.Dto.TrackCreatedRequest;
import com.example.spotifyscrobble.catalog.Dto.TrackCreatedResponse;
import com.example.spotifyscrobble.shared.ArtistNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CatalogService {
    private final ArtistRepository artistRepo;
    private final TrackRepository trackRepo;
    private final ApplicationEventPublisher events;

    public CatalogService(ArtistRepository artistRepo, TrackRepository trackRepo, ApplicationEventPublisher events) {
        this.artistRepo = artistRepo;
        this.trackRepo = trackRepo;
        this.events = events;
    }

    public CatalogEntriesExistResponse getCatalogEntries(Long spotifyTrackId, Long spotifyArtistId){
        ArtistEntity artist = artistRepo.findBySpotifyId(spotifyArtistId).get();
        TrackEntity track = trackRepo.findBySpotifyId(spotifyTrackId).get();
        return new CatalogEntriesExistResponse(artist, track);
    }

    public ArtistCreatedResponse createArtist(ArtistCreatedRequest artistCreatedRequest) {
        ArtistEntity artist = new ArtistEntity(artistCreatedRequest.name(), artistCreatedRequest.spotifyId());
        log.info("Created new artist: {} with spotifyId: {}", artistCreatedRequest.name(), artistCreatedRequest.spotifyId());
        artist = artistRepo.save(artist);
        events.publishEvent(new ArtistCreatedEvent(artist));
        return new ArtistCreatedResponse(artist.getSpotifyId(), artist.getName());
    }

    public TrackCreatedResponse createTrack(TrackCreatedRequest trackCreatedRequest){
        ArtistEntity artist = artistRepo.findBySpotifyId(trackCreatedRequest.spotifyId()).orElseThrow(() -> new ArtistNotFoundException("Artist with spotifyId "+trackCreatedRequest.spotifyId()+" does not exist therefore the track could not be created. Please create an artist first and then create a track under that artist."));
        TrackEntity track = new TrackEntity(trackCreatedRequest.spotifyId(), artist, trackCreatedRequest.title(), trackCreatedRequest.duration());
        track = trackRepo.save(track);
        //Create event
        return new TrackCreatedResponse(track.getSpotifyId(), track.getTitle(), artist.getName(), track.getDuration());
    }
}
