package com.example.spotifyscrobble.statistics.services;

import com.example.spotifyscrobble.catalog.*;
import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import com.example.spotifyscrobble.shared.ArtistAlreadyExistsInStatsException;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.shared.TrackStatAlreadyExists;
import com.example.spotifyscrobble.statistics.UserTopArtistResponse;
import com.example.spotifyscrobble.statistics.UserTopTracksResponse;
import com.example.spotifyscrobble.statistics.components.ArtistStatsUpdater;
import com.example.spotifyscrobble.statistics.components.TrackStatsUpdater;
import com.example.spotifyscrobble.statistics.components.UserStatsUpdater;
import com.example.spotifyscrobble.statistics.entities.ArtistStatsEntity;
import com.example.spotifyscrobble.statistics.entities.TrackStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.*;
import com.example.spotifyscrobble.users.GetUserByIdResponse;
import com.example.spotifyscrobble.users.UsersApi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StatisticsService {
    private final UserArtistStatsRepository userArtistStatsRepo;
    private final UserTrackStatsRepository userTrackStatsRepository;
    private final UsersApi usersApi;
    private final UserStatsUpdater userStatsUpdater;
    private final ArtistStatsUpdater artistStatsUpdater;
    private final TrackStatsUpdater trackStatsUpdater;
    private final ArtistStatsRepository artistStatsRepo;
    private final TrackStatsRepository trackStatsRepository;

    public StatisticsService(UserArtistStatsRepository userArtistStatsRepo,
                             UserTrackStatsRepository userTrackStatsRepo,
                             UsersApi usersApi, UserStatsUpdater userStatsUpdater,
                             ArtistStatsUpdater artistStatsUpdater,
                             TrackStatsUpdater trackStatsUpdater, ArtistStatsRepository artistStatsRepository, TrackStatsRepository trackStatsRepository) {

        this.userArtistStatsRepo = userArtistStatsRepo;
        this.userTrackStatsRepository = userTrackStatsRepo;
        this.usersApi = usersApi;
        this.userStatsUpdater = userStatsUpdater;
        this.artistStatsUpdater = artistStatsUpdater;
        this.trackStatsUpdater = trackStatsUpdater;
        this.artistStatsRepo = artistStatsRepository;
        this.trackStatsRepository = trackStatsRepository;
    }

    @Transactional
    public void handleTrackListenedEvent(CatalogEntriesResolvedEvent event){
        artistStatsUpdater.recordListen(event.artistId(), event.artistName(), event.userId());
        trackStatsUpdater.recordPlay(event.trackId(), event.trackName(), event.userId()); // Need to make track listener table
        userStatsUpdater.recordTrackPlay(event.userId(), event.trackId(), event.trackName(), event.artistName());
        userStatsUpdater.recordArtistPlay(event.userId(), event.artistId(), event.artistName());
    }

    public void createArtistStat(ArtistCreatedEvent event){
        if(artistStatsRepo.existsById(event.artistId())){
            throw new ArtistAlreadyExistsInStatsException("This artist stats already exist.");
        }
        artistStatsRepo.save(new ArtistStatsEntity(event.artistId(), event.artistName()));
    }

    public void createTrackStat(TrackCreatedEvent event){
        if(trackStatsRepository.existsById(event.trackId())){
            throw new TrackStatAlreadyExists("This tracks stats already exist");
        }
        trackStatsRepository.save(new TrackStatsEntity(event.trackId(), event.title()));
    }

    public CustomPageResponse<UserTopTracksResponse> getUsersTopTracks(String username, Pageable page){
        Pageable sortedPage = PageRequest.of(
                page.getPageNumber(),
                page.getPageSize(),
                Sort.by(Sort.Direction.DESC, "playCount")
        );
        GetUserByIdResponse response = usersApi.getUserByUsername(username);
        Page<UserTopTracksResponse> tracks = userTrackStatsRepository.findAllById_UserId(response.id(), sortedPage)
                .map(entity -> new UserTopTracksResponse(
                        entity.getTrackName(),
                        entity.getArtistName(),
                        entity.getPlayCount()
                ));
        return new CustomPageResponse<>(tracks);
    }

    public CustomPageResponse<UserTopArtistResponse> getUsersTopArtists(String username, Pageable page){
        Pageable sortedPage = PageRequest.of(
                page.getPageNumber(),
                page.getPageSize(),
                Sort.by(Sort.Direction.DESC, "playCount")
        );

        GetUserByIdResponse response = usersApi.getUserByUsername(username);
        Page<UserTopArtistResponse> artists = userArtistStatsRepo.findAllById_UserId(response.id(), sortedPage)
                .map(entity -> new UserTopArtistResponse(
                        entity.getArtistName(),
                        entity.getPlayCount()
                ));
        return new CustomPageResponse<>(artists);
    }

}
