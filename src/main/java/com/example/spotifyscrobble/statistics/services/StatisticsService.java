package com.example.spotifyscrobble.statistics.services;

import com.example.spotifyscrobble.catalog.*;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.statistics.UserTopArtistResponse;
import com.example.spotifyscrobble.statistics.UserTopTracksResponse;
import com.example.spotifyscrobble.statistics.components.ArtistStatsUpdater;
import com.example.spotifyscrobble.statistics.components.TrackStatsUpdater;
import com.example.spotifyscrobble.statistics.components.UserStatsUpdater;
import com.example.spotifyscrobble.statistics.repositories.*;
import com.example.spotifyscrobble.users.UsersApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StatisticsService {
    private final UserArtistStatsRepository userArtistStatsRepo;
    private final UserTrackStatsRepository userTrackStatsRepository;
    private final UsersApi usersApi;
    private final UserStatsUpdater userStatsUpdater;
    private final ArtistStatsUpdater artistStatsUpdater;
    private final TrackStatsUpdater trackStatsUpdater;

    public StatisticsService(UserArtistStatsRepository userArtistStatsRepo,
                             UserTrackStatsRepository userTrackStatsRepo,
                             UsersApi usersApi, UserStatsUpdater userStatsUpdater,
                             ArtistStatsUpdater artistStatsUpdater,
                             TrackStatsUpdater trackStatsUpdater) {

        this.userArtistStatsRepo = userArtistStatsRepo;
        this.userTrackStatsRepository = userTrackStatsRepo;
        this.usersApi = usersApi;
        this.userStatsUpdater = userStatsUpdater;
        this.artistStatsUpdater = artistStatsUpdater;
        this.trackStatsUpdater = trackStatsUpdater;
    }

    @Transactional
    public void handleTrackListenedEvent(CatalogEntriesResolvedEvent event){
        artistStatsUpdater.recordListen(event.artistId(), event.artistName(), event.userId());
        trackStatsUpdater.recordPlay(event.trackId(), event.trackName()); // Need to make track listener table
        userStatsUpdater.recordTrackPlay(event.userId(), event.trackId(), event.trackName(), event.artistName());
        userStatsUpdater.recordArtistPlay(event.userId(), event.artistId(), event.artistName());
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

    public CustomPageResponse<UserTopArtistResponse> getUsersTopArtists(Long userId, int page, int size){
        Pageable p = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "playCount"));
        Page<UserTopArtistResponse> artists = userArtistStatsRepo.findAllById_UserId(userId, p)
                .map(entity -> new UserTopArtistResponse(
                        entity.getArtistName(),
                        entity.getPlayCount()
                ));
        return new CustomPageResponse<>(artists);
    }

}
