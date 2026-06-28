package com.example.spotifyscrobble.statistics;

import com.example.spotifyscrobble.catalog.*;
import com.example.spotifyscrobble.catalog.ArtistEntity;
import com.example.spotifyscrobble.catalog.TrackEntity;
import com.example.spotifyscrobble.statistics.entities.*;
import com.example.spotifyscrobble.statistics.repositories.UserArtistStatsRepository;
import com.example.spotifyscrobble.statistics.repositories.UserTrackStatsRepository;
import com.example.spotifyscrobble.users.UserEntity;
import com.example.spotifyscrobble.users.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.spotifyscrobble.statistics.repositories.ArtistStatsRepository;
import com.example.spotifyscrobble.statistics.repositories.TrackStatsRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatisticsService {
    private final ArtistStatsRepository artistStatsRepo;
    private final TrackStatsRepository trackStatsRepo;
    private final ArtistListenerRepository artistListenerRepo;
    private final UserRepository userRepo;
    private final UserArtistStatsRepository userArtistStatsRepo;
    private final UserTrackStatsRepository userTrackStatsRepository;

    public StatisticsService(ArtistStatsRepository artistStatsRepo, TrackStatsRepository trackStatsRepo, ArtistListenerRepository artistListenerRepo, UserRepository userRepo, UserArtistStatsRepository userArtistStatsRepo, UserTrackStatsRepository userTrackStatsRepository) {
        this.artistStatsRepo = artistStatsRepo;
        this.trackStatsRepo = trackStatsRepo;
        this.artistListenerRepo = artistListenerRepo;
        this.userRepo = userRepo;
        this.userArtistStatsRepo = userArtistStatsRepo;
        this.userTrackStatsRepository = userTrackStatsRepository;
    }

    public void handleTrackListenedEvent(CatalogEntriesResolvedEvent event){
        initializeNewArtistStats(event.artist());
        initializeNewTrackStats(event.track());

        UserEntity user = userRepo.findById(event.userId()).orElseThrow(() -> new IllegalArgumentException("User does not exist"));
        initializeNewUserArtistStatsOrIncrement(event.userId(), event.artist().getArtistId());
        initializeNewUserTrackStatsOrIncrement(event.userId(), event.track().getTrackId());

        if(!isExistingArtistListener(event.artist(), user)){
            artistStatsRepo.incrementListeners(event.artist().getArtistId());
        }else{
            artistStatsRepo.incrementTotalPlays(event.artist().getArtistId());
            trackStatsRepo.incrementTrackPlays(event.track().getTrackId());
        };

    }

    public void initializeNewArtistStats(ArtistEntity artist){
        if (!artistStatsRepo.existsById(artist.getArtistId())) {
            artistStatsRepo.save(new ArtistStatsEntity(artist));
        }
    }

    public void initializeNewTrackStats(TrackEntity track){
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

    public void initializeNewUserArtistStatsOrIncrement(Long userId, Long artistId){
        UserArtistStatsId id = new UserArtistStatsId(userId, artistId);
        userArtistStatsRepo.findById(id).ifPresentOrElse(
                stats -> userArtistStatsRepo.incrementTotalPlays(id),
                () -> userArtistStatsRepo.save(new UserArtistStatsEntity(id)) // just save, no increment
        );
    }

    public void initializeNewUserTrackStatsOrIncrement(Long userId, Long trackId){
        UserTrackStatsId id = new UserTrackStatsId(userId, trackId);
        userTrackStatsRepository.findById(id).ifPresentOrElse(
                stats -> userTrackStatsRepository.incrementTotalPlays(id),
                () -> userTrackStatsRepository.save(new UserTrackStatsEntity(id))
        );
    }
}
