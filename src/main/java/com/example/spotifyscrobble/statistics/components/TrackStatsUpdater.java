package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.ArtistListenerEntity;
import com.example.spotifyscrobble.statistics.entities.TrackListenerEntity;
import com.example.spotifyscrobble.statistics.entities.TrackStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.TrackListenerRepository;
import com.example.spotifyscrobble.statistics.repositories.TrackStatsRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class TrackStatsUpdater {
    private final TrackStatsRepository trackStatsRepo;
    private final IsExistingListener isExistingListener;

    public TrackStatsUpdater(TrackStatsRepository trackStatsRepo, IsExistingListener isExistingListener) {
        this.trackStatsRepo = trackStatsRepo;
        this.isExistingListener = isExistingListener;
    }

    @Transactional
    public void recordPlay(Long trackId, String trackName, UUID userId){ //Need listener table
        if(!trackStatsRepo.existsById(trackId)){
            trackStatsRepo.save(new TrackStatsEntity(trackId, trackName));
        }

        if(!isExistingListener.isExistingTrackListener(userId, trackId)){
            trackStatsRepo.incrementListeners(trackId);
        }
        trackStatsRepo.incrementTrackPlays(trackId);
    }


}
