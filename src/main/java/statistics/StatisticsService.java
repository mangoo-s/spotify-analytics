package statistics;

import Catalog.entity.ArtistEntity;
import Catalog.entity.TrackEntity;
import Catalog.repository.ArtistRepository;
import org.springframework.stereotype.Service;
import statistics.entity.ArtistStatsEntity;
import statistics.entity.TrackStatsEntity;
import statistics.repositories.ArtistStatsRepository;
import statistics.repositories.TrackStatsRepository;

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
