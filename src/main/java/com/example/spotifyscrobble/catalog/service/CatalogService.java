package com.example.spotifyscrobble.catalog.service;

import com.example.spotifyscrobble.catalog.ArtistCreatedEvent;
import com.example.spotifyscrobble.catalog.CatalogApi;
import com.example.spotifyscrobble.catalog.GetArtistAndTrackbyTrackIdDto;
import com.example.spotifyscrobble.catalog.TrackCreatedEvent;
import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import com.example.spotifyscrobble.catalog.entity.TrackEntity;
import com.example.spotifyscrobble.catalog.internalDto.*;
import com.example.spotifyscrobble.catalog.repository.ArtistRepository;
import com.example.spotifyscrobble.catalog.repository.TrackRepository;
import com.example.spotifyscrobble.shared.ArtistAlreadyExists;
import com.example.spotifyscrobble.shared.ArtistNotFoundException;
import com.example.spotifyscrobble.shared.TrackAlreadyExistsException;
import com.example.spotifyscrobble.shared.TrackNotFoundException;
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

    public TrackCreatedResponse createTrack(TrackCreatedRequest trackCreatedRequest){ //Still needs testing
        ArtistEntity artist = artistRepo.findBySpotifyId(trackCreatedRequest.artistSpotifyId()).orElseThrow(() -> new ArtistNotFoundException("Artist with spotifyId "+trackCreatedRequest.spotifyId()+" does not exist therefore the track could not be created. Please create an artist first and then create a track under that artist."));
        if(trackRepo.existsBySpotifyId(trackCreatedRequest.spotifyId())){ throw new TrackAlreadyExistsException("This track already exists."); }

        TrackEntity track = new TrackEntity(trackCreatedRequest.spotifyId(), artist, trackCreatedRequest.title(), trackCreatedRequest.duration());
        track = trackRepo.save(track);
        log.info("Created new track with title: {} and artistSpotifyId: {} and trackSpotifyId: {}", trackCreatedRequest.title(), artist.getSpotifyId(), trackCreatedRequest.spotifyId());
        events.publishEvent(new TrackCreatedEvent(track));
        return new TrackCreatedResponse(track.getSpotifyId(), track.getTitle(), artist.getName(), track.getDuration());
    }

    @Override
    @Cacheable(value = "artistAndTrack", key = "#trackId")
    public GetArtistAndTrackbyTrackIdDto getArtistAndTrackByTrackId(long trackId){
        TrackEntity track = trackRepo.findById(trackId).orElseThrow(() -> new TrackNotFoundException("This track does not exist."));
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

    public GetArtistResponse getArtist(long id){
        ArtistEntity artist = artistRepo.findById(id).orElseThrow(() -> new ArtistNotFoundException("This artist does not exist."));
        return new GetArtistResponse(artist.getName(), artist.getSpotifyId());
    }

    public GetTrackResponse getTrack(Long id){
        TrackEntity track = trackRepo.findById(id).orElseThrow(() -> new TrackNotFoundException("This track does not exist"));
        return new GetTrackResponse(track.getTitle(), track.getArtist().getName(), track.getSpotifyId(), track.getArtist().getSpotifyId(), track.getDuration());
    }

    public void deleteArtist(long id){
        artistRepo.deleteById(id);
        //Need to create an event that deletes all info on artist
    }

    public void deleteTrack(long id){
        trackRepo.deleteById(id);
        //need to create an event that deletes all info on track
    }

    public void updateArtist(long id, UpdateArtistRequest updateArtistRequest){
        ArtistEntity artist = artistRepo.findById(id).orElseThrow(() -> new ArtistNotFoundException("This artist does not exist"));
        if(artistRepo.existsBySpotifyId(updateArtistRequest.spotifyId())){
            throw new ArtistAlreadyExists("An artist with this spotifyId already exists.");
        }

        if(!updateArtistRequest.name().isBlank()){
            artist.setName(updateArtistRequest.name());
        }

        if(!updateArtistRequest.spotifyId().isBlank()){
            artist.setSpotifyId(updateArtistRequest.spotifyId());
        }        artistRepo.save(artist);

    }

    public void updateTrack(long id, UpdateTrackRequest updateTrackRequest){
        TrackEntity track = trackRepo.findById(id).orElseThrow(() -> new TrackNotFoundException("This track does not exist."));
        if(trackRepo.existsBySpotifyId(updateTrackRequest.trackSpotifyId())){
            throw new TrackAlreadyExistsException("This track already exists");
        }

        if(!updateTrackRequest.name().isBlank()){
            track.setTitle(updateTrackRequest.name());
        }

        if(!updateTrackRequest.trackSpotifyId().isBlank()){
            track.setSpotifyId(updateTrackRequest.trackSpotifyId());
        }

        if(updateTrackRequest.duration() != null){
            track.setDuration(updateTrackRequest.duration());
        }
        trackRepo.save(track);
    }



}
