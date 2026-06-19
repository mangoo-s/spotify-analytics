package com.example.spotifyscrobble.statistics;

import com.example.spotifyscrobble.listening.TrackListenedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import com.example.spotifyscrobble.statistics.entity.ArtistStatsEntity;
import com.example.spotifyscrobble.statistics.entity.TrackStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.ArtistStatsRepository;
import com.example.spotifyscrobble.statistics.repositories.TrackStatsRepository;
import com.example.spotifyscrobble.users.UserRepository;

@Component
@Slf4j
public class StatisticsEventListener {
    private final ArtistStatsRepository artistStatsRepo;
    private final TrackStatsRepository trackStatsRepo;
    private final UserRepository userRepo;
    private final StatisticsService statsService;

    public StatisticsEventListener(ArtistStatsRepository artistRepo, TrackStatsRepository trackRepo, UserRepository userRepo, StatisticsService statsService){
        this.artistStatsRepo = artistRepo;
        this.trackStatsRepo = trackRepo;
        this.userRepo = userRepo;
        this.statsService = statsService;
    }

    @ApplicationModuleListener
    public void onTrackListenedEvent(TrackListenedEvent event){
        log.info("TrackListenedEvent has been received by StatisticsEventListener.");
        ArtistStatsEntity artistStats = statsService.createNewArtistStats(event.artist());
        TrackStatsEntity trackStats = statsService.createNewTrackStats(event.track());

        artistStatsRepo.incrementTotalPlays(event.artist().getArtistId());
        trackStatsRepo.incrementTrackPlays(event.track().getTrackId());

    }
}
