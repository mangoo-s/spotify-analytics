package com.example.spotifyscrobble.statistics;

import com.example.spotifyscrobble.catalog.ArtistEntity;
import com.example.spotifyscrobble.catalog.TrackEntity;
import com.example.spotifyscrobble.listening.TrackListenedEvent;
import com.example.spotifyscrobble.users.UserEntity;
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

    public StatisticsService(ArtistStatsRepository artistStatsRepo, TrackStatsRepository trackStatsRepo, ArtistListenerRepository artistListenerRepo) {
        this.artistStatsRepo = artistStatsRepo;
        this.trackStatsRepo = trackStatsRepo;
        this.artistListenerRepo = artistListenerRepo;
    }

    @Transactional
    public void handleTrackListenedEvent(TrackListenedEvent event){
        createNewArtistStats(event.artist());
        createNewTrackStats(event.track());
        if(!isExistingArtistListener(event.artist(), event.user())){
            artistStatsRepo.incrementListeners(event.artist().getArtistId());
        };

        artistStatsRepo.incrementTotalPlays(event.artist().getArtistId());
        trackStatsRepo.incrementTrackPlays(event.track().getTrackId());

    }

    public void createNewArtistStats(ArtistEntity artist){
        try{
            artistStatsRepo.save(new ArtistStatsEntity(artist));
        } catch(DataIntegrityViolationException ignored){}
    }

    public void createNewTrackStats(TrackEntity track){
        try{
            trackStatsRepo.save(new TrackStatsEntity(track));
        } catch(DataIntegrityViolationException ignored){}
    }

    public boolean isExistingArtistListener(ArtistEntity artist, UserEntity user){
        try {
            if(artistListenerRepo.existsByArtist_ArtistIdAndUser_UserId(artist.getArtistId(), user.getUserId())){
                return true;
            }
            artistListenerRepo.save(new ArtistListenerEntity(artist, user));
            return false;
        } catch (DataIntegrityViolationException ignored) {
            return true;
        }
    }

}
