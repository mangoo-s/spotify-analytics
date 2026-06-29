package com.example.spotifyscrobble.statistics.services;

import com.example.spotifyscrobble.catalog.*;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.statistics.UserTopTracksResponse;
import com.example.spotifyscrobble.statistics.entities.*;
import com.example.spotifyscrobble.statistics.repositories.*;
import com.example.spotifyscrobble.users.UsersApi;
import com.example.spotifyscrobble.users.entity.UserEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StatisticsService {
    private final ArtistStatsRepository artistStatsRepo;
    private final TrackStatsRepository trackStatsRepo;
    private final ArtistListenerRepository artistListenerRepo;
    private final UserArtistStatsRepository userArtistStatsRepo;
    private final UserTrackStatsRepository userTrackStatsRepository;
    private final UsersApi usersApi;

    public StatisticsService(ArtistStatsRepository artistStatsRepo, TrackStatsRepository trackStatsRepo, ArtistListenerRepository artistListenerRepo, UserArtistStatsRepository userArtistStatsRepo, UserTrackStatsRepository userTrackStatsRepository, UsersApi usersApi) {
        this.artistStatsRepo = artistStatsRepo;
        this.trackStatsRepo = trackStatsRepo;
        this.artistListenerRepo = artistListenerRepo;
        this.userArtistStatsRepo = userArtistStatsRepo;
        this.userTrackStatsRepository = userTrackStatsRepository;
        this.usersApi = usersApi;
    }

    public void handleTrackListenedEvent(CatalogEntriesResolvedEvent event){
        initializeNewArtistStats(event.artistId());
        initializeNewTrackStats(event.trackId());

        initializeNewUserArtistStatsOrIncrement(event.userId(), event.artistId(), event.artistName());
        initializeNewUserTrackStatsOrIncrement(event.userId(), event.trackId(), event.artistName(), event.trackName());

        if(!isExistingArtistListener(event.artistId(), event.userId())){
            artistStatsRepo.incrementListeners(event.artistId());
        }
        artistStatsRepo.incrementTotalPlays(event.artistId());
        trackStatsRepo.incrementTrackPlays(event.trackId());

    }

    public CustomPageResponse<UserTopTracksResponse> getUsersTopTracks(Long userId, int page, int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "playCount"));
        Page<UserTopTracksResponse> tracks = userTrackStatsRepository.findAllById_UserId(userId, pageable)
                .map(entity -> new UserTopTracksResponse(
                        entity.getTrackName(),
                        entity.getArtistName(),
                        entity.getPlayCount()
                ));
        return new CustomPageResponse<>(tracks);
    }

    public void initializeNewArtistStats(Long artistId){
        if (!artistStatsRepo.existsById(artistId)) {
            artistStatsRepo.save(new ArtistStatsEntity(artistId));
        }
    }

    public void initializeNewTrackStats(Long trackId){
        if (!trackStatsRepo.existsById(trackId)) {
            trackStatsRepo.save(new TrackStatsEntity(trackId));
        }
    }

    public boolean isExistingArtistListener(Long artistId, Long userId){
        try {
            if(!artistListenerRepo.existsByArtistIdAndUserId(artistId, userId)){
                artistListenerRepo.save(new ArtistListenerEntity(artistId, userId));
                return false;
            }
            return true;
        } catch (DataIntegrityViolationException ignored) {
            return true;
        }
    }

    public void initializeNewUserArtistStatsOrIncrement(Long userId, Long artistId, String artistName){
        UserArtistStatsId id = new UserArtistStatsId(userId, artistId);
        userArtistStatsRepo.findById(id).ifPresentOrElse(
                stats -> userArtistStatsRepo.incrementTotalPlays(id),
                () -> userArtistStatsRepo.save(new UserArtistStatsEntity(id, artistName)) // just save, no increment
        );
    }

    public void initializeNewUserTrackStatsOrIncrement(Long userId, Long trackId, String trackName, String artistName){
        UserTrackStatsId id = new UserTrackStatsId(userId, trackId);
        userTrackStatsRepository.findById(id).ifPresentOrElse(
                stats -> userTrackStatsRepository.incrementTotalPlays(id),
                () -> userTrackStatsRepository.save(new UserTrackStatsEntity(id, artistName, trackName))
        );
    }
}
