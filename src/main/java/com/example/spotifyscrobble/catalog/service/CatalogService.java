package com.example.spotifyscrobble.catalog.service;

import com.example.spotifyscrobble.catalog.ArtistCreatedEvent;
import com.example.spotifyscrobble.catalog.CatalogApi;
import com.example.spotifyscrobble.catalog.GetArtistAndTrackbyTrackIdDto;
import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import com.example.spotifyscrobble.catalog.entity.TrackEntity;
import com.example.spotifyscrobble.catalog.internalDto.ArtistCreatedRequest;
import com.example.spotifyscrobble.catalog.internalDto.ArtistCreatedResponse;
import com.example.spotifyscrobble.catalog.internalDto.TrackCreatedRequest;
import com.example.spotifyscrobble.catalog.internalDto.TrackCreatedResponse;
import com.example.spotifyscrobble.catalog.repository.ArtistRepository;
import com.example.spotifyscrobble.catalog.repository.TrackRepository;
import com.example.spotifyscrobble.shared.ArtistAlreadyExists;
import com.example.spotifyscrobble.shared.ArtistNotFoundException;
import com.example.spotifyscrobble.users.dto.Track;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CatalogService implements CatalogApi {
    private final ArtistRepository artistRepo;
    private final TrackRepository trackRepo;
    private final ApplicationEventPublisher events;

    public CatalogService(ArtistRepository artistRepo, TrackRepository trackRepo, ApplicationEventPublisher events) {
        this.artistRepo = artistRepo;
        this.trackRepo = trackRepo;
        this.events = events;
    }


    public ArtistCreatedResponse createArtist(ArtistCreatedRequest artistCreatedRequest) {
        if(artistRepo.existsBySpotifyId(artistCreatedRequest.spotifyId())) { throw new ArtistAlreadyExists("This artist already exists."); }

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
        log.info("Created new track with title: {} and artistSpotifyId: {} and trackSpotifyId: {}", trackCreatedRequest.title(), artist.getSpotifyId(), trackCreatedRequest.spotifyId());
        //Create event
        return new TrackCreatedResponse(track.getSpotifyId(), track.getTitle(), artist.getName(), track.getDuration());
    }

    @Override
    @Cacheable(value = "artistAndTrack", key = "#trackId")
    public GetArtistAndTrackbyTrackIdDto getArtistAndTrackByTrackId(long trackId){
        TrackEntity track = trackRepo.findById(trackId).orElseThrow(() -> new RuntimeException("Test")); //Need custom exception
        return new GetArtistAndTrackbyTrackIdDto(
                track.getArtist().getName(),
                track.getTitle()
        );
    }

    @Override
    @Cacheable(value = "artistName", key = "#artistID")
    public String getArtistNameById(long artistID){
        ArtistEntity artist = artistRepo.findById(artistID).orElseThrow(() -> new ArtistNotFoundException("This artist does not exist"));
        return artist.getName();
    }



}
