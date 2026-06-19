package com.example.spotifyscrobble.statistics;

import com.example.spotifyscrobble.catalog.ArtistEntity;
import com.example.spotifyscrobble.catalog.CatalogEntriesExistResponse;
import com.example.spotifyscrobble.catalog.CatalogService;
import com.example.spotifyscrobble.catalog.TrackEntity;
import com.example.spotifyscrobble.listening.TrackListenedEvent;
import com.example.spotifyscrobble.users.UserEntity;
import com.example.spotifyscrobble.users.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.example.spotifyscrobble.statistics.entity.ArtistStatsEntity;
import com.example.spotifyscrobble.statistics.entity.TrackStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.ArtistStatsRepository;
import com.example.spotifyscrobble.statistics.repositories.TrackStatsRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {
    private final ArtistStatsRepository artistStatsRepo;
    private final TrackStatsRepository trackStatsRepo;
    private final ArtistListenerRepository artistListenerRepo;
    private final CatalogService catalogService;
    private final UserRepository userRepo;

    public StatisticsService(ArtistStatsRepository artistStatsRepo, TrackStatsRepository trackStatsRepo, ArtistListenerRepository artistListenerRepo, CatalogService catalogService, UserRepository userRepo) {
        this.artistStatsRepo = artistStatsRepo;
        this.trackStatsRepo = trackStatsRepo;
        this.artistListenerRepo = artistListenerRepo;
        this.catalogService = catalogService;
        this.userRepo = userRepo;
    }

    @Transactional
    public void handleTrackListenedEvent(TrackListenedEvent event){
        CatalogEntriesExistResponse response = catalogService.getCatalogEntries(event.trackId(), event.artistId());
        createNewArtistStats(response.artist());
        createNewTrackStats(response.track());
        UserEntity user = userRepo.findById(event.userId()).orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        if(!isExistingArtistListener(response.artist(), user)){
            System.out.println("HI from if statement");
            artistStatsRepo.incrementListeners(response.artist().getArtistId());
        };

        System.out.println("HI5");
        artistStatsRepo.incrementTotalPlays(response.artist().getArtistId());
        System.out.println("HI6");
        trackStatsRepo.incrementTrackPlays(response.track().getTrackId());

    }

    public void createNewArtistStats(ArtistEntity artist){
        if (!artistStatsRepo.existsById(artist.getArtistId())) {
            artistStatsRepo.save(new ArtistStatsEntity(artist));
        }
    }

    public void createNewTrackStats(TrackEntity track){
        if (!trackStatsRepo.existsById(track.getTrackId())) {
            trackStatsRepo.save(new TrackStatsEntity(track));
        }
    }

    public boolean isExistingArtistListener(ArtistEntity artist, UserEntity user){
        try {
            if(!artistListenerRepo.existsByArtist_ArtistIdAndUser_UserId(artist.getArtistId(), user.getUserId())){
                artistListenerRepo.save(new ArtistListenerEntity(artist, user));
                return false;
            }
            return true;
        } catch (DataIntegrityViolationException ignored) {
            return true;
        }
    }

}
