package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.TrackStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.TrackStatsRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TrackStatsUpdater {
    private final TrackStatsRepository trackStatsRepo;

    public TrackStatsUpdater(TrackStatsRepository trackStatsRepo) {
        this.trackStatsRepo = trackStatsRepo;
    }

    @Transactional
    public void recordPlay(Long trackId, String trackName){ //Need listener table
        if(!trackStatsRepo.existsById(trackId)){
            trackStatsRepo.save(new TrackStatsEntity(trackId, trackName));
        }
        trackStatsRepo.incrementTrackPlays(trackId);
    }
}
