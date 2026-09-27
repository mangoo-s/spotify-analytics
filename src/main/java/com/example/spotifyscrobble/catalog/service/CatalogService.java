package com.example.spotifyscrobble.catalog.service;

import com.example.spotifyscrobble.catalog.*;
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
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;

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


    public ArtistCreatedResponse createArtist(ArtistCreatedRequest artistCreatedRequest) {
        if(artistRepo.existsBySpotifyIdAndDeletedAtIsNull(artistCreatedRequest.spotifyId())) { throw new ArtistAlreadyExists("This artist already exists."); }

        ArtistEntity artist = new ArtistEntity(artistCreatedRequest.name(), artistCreatedRequest.spotifyId());
        log.info("Created new artist: {} with spotifyId: {}", artistCreatedRequest.name(), artistCreatedRequest.spotifyId());
        artist = artistRepo.save(artist);
        events.publishEvent(new ArtistCreatedEvent(artist.getArtistId(), artist.getName(), artist.getSpotifyId()));
        return new ArtistCreatedResponse(artist.getSpotifyId(), artist.getName());
    }

    public TrackCreatedResponse createTrack(TrackCreatedRequest trackCreatedRequest){
        ArtistEntity artist = artistRepo.findBySpotifyIdAndDeletedAtIsNull(trackCreatedRequest.artistSpotifyId()).orElseThrow(() -> new ArtistNotFoundException("Artist with spotifyId "+trackCreatedRequest.spotifyId()+" does not exist therefore the track could not be created. Please create an artist first and then create a track under that artist."));
        if(trackRepo.existsBySpotifyIdAndDeletedAtIsNull(trackCreatedRequest.spotifyId())){ throw new TrackAlreadyExistsException("This track already exists."); }

        TrackEntity track = new TrackEntity(trackCreatedRequest.spotifyId(), artist, trackCreatedRequest.title(), trackCreatedRequest.duration());
        track = trackRepo.save(track);
        log.info("Created new track with title: {} and artistSpotifyId: {} and trackSpotifyId: {}", trackCreatedRequest.title(), artist.getSpotifyId(), trackCreatedRequest.spotifyId());
        events.publishEvent(new TrackCreatedEvent(track.getTrackId(), track.getTitle(), track.getSpotifyId()));
        return new TrackCreatedResponse(track.getSpotifyId(), track.getTitle(), artist.getName(), track.getDuration());
    }

    @Cacheable(value = "artistCache", key = "#id")
    public ArtistCacheView getArtist(long id){
        ArtistEntity artist = artistRepo.findByArtistIdAndDeletedAtIsNull(id).orElseThrow(() -> new ArtistNotFoundException("This artist does not exist."));
        return new ArtistCacheView(artist.getArtistId(), artist.getName(), artist.getSpotifyId());
    }

    @Cacheable(value = "trackCache", key = "#id")
    public TrackCacheView getTrack(long id){
        TrackEntity track = trackRepo.findByTrackIdAndDeletedAtIsNull(id).orElseThrow(() -> new TrackNotFoundException("This track does not exist"));
        return new TrackCacheView(track.getTrackId(),
                track.getTitle(), track.getSpotifyId(),
                track.getDuration(),
                track.getArtist().getName(),
                track.getArtist().getSpotifyId(),
                track.getArtist().getArtistId());
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "artistCache", key = "#id"),
            }
    )
    public void deleteArtist(long id){
        ArtistEntity artist = artistRepo.findByArtistIdAndDeletedAtIsNull(id).orElseThrow(() -> new ArtistNotFoundException("This artist does not exist"));

        artist.setDeletedAt(Instant.now());
        artistRepo.save(artist);
        events.publishEvent(new ArtistDeletedEvent(id));
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "trackCache", key = "#id")
            }
    )
    public void deleteTrack(long id){
        TrackEntity track = trackRepo.findByTrackIdAndDeletedAtIsNull(id).orElseThrow(() -> new TrackNotFoundException("This track does not exist."));

        track.setDeletedAt(Instant.now());
        trackRepo.save(track);
        events.publishEvent(new TrackDeletedEvent(id));
    }

    @CacheEvict(value = "artistCache", key = "#id")
    public void updateArtist(long id, UpdateArtistRequest updateArtistRequest){
        ArtistEntity artist = artistRepo.findByArtistIdAndDeletedAtIsNull(id).orElseThrow(() -> new ArtistNotFoundException("This artist does not exist"));

        if(!updateArtistRequest.name().isBlank()){
            artist.setName(updateArtistRequest.name());
        }

        if(!updateArtistRequest.spotifyId().isBlank()){
            if(artistRepo.existsBySpotifyIdAndDeletedAtIsNull(updateArtistRequest.spotifyId())){
                throw new ArtistAlreadyExists("An artist with this spotifyId already exists.");
            }
            artist.setSpotifyId(updateArtistRequest.spotifyId());
        }
        artistRepo.save(artist);

    }

    @Caching(
            evict = {
                    @CacheEvict(value = "trackCache", key = "#id")
            }
    )
    public void updateTrack(long id, UpdateTrackRequest updateTrackRequest){
        TrackEntity track = trackRepo.findByTrackIdAndDeletedAtIsNull(id).orElseThrow(() -> new TrackNotFoundException("This track does not exist."));

        if(!updateTrackRequest.name().isBlank()){
            track.setTitle(updateTrackRequest.name());
        }

        if(!updateTrackRequest.trackSpotifyId().isBlank()){
            if(trackRepo.existsBySpotifyIdAndDeletedAtIsNull(updateTrackRequest.trackSpotifyId())){
                throw new TrackAlreadyExistsException("This track already exists");
            }
            track.setSpotifyId(updateTrackRequest.trackSpotifyId());
        }

        if(updateTrackRequest.duration() != null){
            track.setDuration(updateTrackRequest.duration());
        }
        trackRepo.save(track);
    }



}
