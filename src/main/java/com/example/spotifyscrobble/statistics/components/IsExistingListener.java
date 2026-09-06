package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.ArtistListenerEntity;
import com.example.spotifyscrobble.statistics.entities.TrackListenerEntity;
import com.example.spotifyscrobble.statistics.repositories.ArtistListenerRepository;
import com.example.spotifyscrobble.statistics.repositories.TrackListenerRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IsExistingListener {
    private final TrackListenerRepository trackListenerRepo;
    private final ArtistListenerRepository artistListenerRepo;

    public IsExistingListener(TrackListenerRepository trackListenerRepo, ArtistListenerRepository artistListenerRepo) {
        this.trackListenerRepo = trackListenerRepo;
        this.artistListenerRepo = artistListenerRepo;
    }

    @Cacheable(cacheNames = "isListener", key = "'track_' + #userId +'_'+ #trackId", unless = "#result == false")
    public boolean isExistingTrackListener(UUID userId, Long trackId){
        try {
            if(!trackListenerRepo.existsByTrackIdAndUserId(trackId, userId)){
                trackListenerRepo.save(new TrackListenerEntity(trackId, userId));
                return false;
            }
            return true;
        } catch (DataIntegrityViolationException ignored) {
            return true;
        }
    }

    @Cacheable(cacheNames = "isListener", key = "'artist_' + #userId +'_'+ #artistId", unless = "#result == false")
    public boolean isExistingArtistListener(UUID userId, Long artistId){
        try {
            if(!artistListenerRepo.existsByArtistIdAndUserId(artistId, userId)){
                artistListenerRepo.save(new ArtistListenerEntity(artistId, userId));
                return false;
            }
            return true;
        } catch (DataIntegrityViolationException ignored) {
            return true;
        }
    }
}
