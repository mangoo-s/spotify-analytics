package statistics;

import listening.TrackListenedEvent;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;
import statistics.entity.ArtistStatsEntity;
import statistics.entity.TrackStatsEntity;
import statistics.repositories.ArtistStatsRepository;
import statistics.repositories.TrackStatsRepository;
import users.UserRepository;

@Component
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
        ArtistStatsEntity artistStats = statsService.createNewArtistStats(event.artist());
        TrackStatsEntity trackStats = statsService.createNewTrackStats(event.track());

        artistStatsRepo.incrementTotalPlays(event.artist().getArtistId());
        trackStatsRepo.incrementTrackPlays(event.track().getTrackId());

    }
}
