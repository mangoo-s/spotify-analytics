package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.ArtistListenerEntity;
import com.example.spotifyscrobble.statistics.entities.TrackListenerEntity;
import com.example.spotifyscrobble.statistics.entities.TrackStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.TrackListenerRepository;
import com.example.spotifyscrobble.statistics.repositories.TrackStatsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class TrackStatsUpdater {
    private final TrackStatsRepository trackStatsRepo;
    private final TrackListenerRepository trackListenerRepo;

    public TrackStatsUpdater(TrackStatsRepository trackStatsRepo, TrackListenerRepository trackListenerRepo) {
        this.trackStatsRepo = trackStatsRepo;
        this.trackListenerRepo = trackListenerRepo;
    }

    @Transactional
    public void recordPlay(Long trackId, String trackName, UUID userId){ //Need listener table
        if(!trackStatsRepo.existsById(trackId)){
            trackStatsRepo.save(new TrackStatsEntity(trackId, trackName));
        }

        if(!isExistingListener(userId, trackId)){
            trackStatsRepo.incrementTrackPlays(trackId);
        }
        trackStatsRepo.incrementTrackPlays(trackId);
    }

    private boolean isExistingListener(UUID userId, Long trackId){
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
}
