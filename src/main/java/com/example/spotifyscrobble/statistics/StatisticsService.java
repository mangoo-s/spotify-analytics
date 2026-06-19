package com.example.spotifyscrobble.statistics;

import com.example.spotifyscrobble.catalog.ArtistEntity;
import com.example.spotifyscrobble.catalog.TrackEntity;
import org.springframework.stereotype.Service;
import com.example.spotifyscrobble.statistics.entity.ArtistStatsEntity;
import com.example.spotifyscrobble.statistics.entity.TrackStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.ArtistStatsRepository;
import com.example.spotifyscrobble.statistics.repositories.TrackStatsRepository;

@Service
public class StatisticsService {
    private final ArtistStatsRepository artistStatsRepo;
    private final TrackStatsRepository trackStatsRepo;

    public StatisticsService(ArtistStatsRepository artistStatsRepo, TrackStatsRepository trackStatsRepo) {
        this.artistStatsRepo = artistStatsRepo;
        this.trackStatsRepo = trackStatsRepo;
    }

    public ArtistStatsEntity createNewArtistStats(ArtistEntity artist){
        return artistStatsRepo.findById(artist.getArtistId())
                .orElseGet(() -> artistStatsRepo.save(
                        new ArtistStatsEntity(artist)
                ));
    }

    public TrackStatsEntity createNewTrackStats(TrackEntity track){
        return trackStatsRepo.findById(track.getTrackId())
                .orElseGet(() -> trackStatsRepo.save(
                        new TrackStatsEntity(track)
                ));
    }

}
