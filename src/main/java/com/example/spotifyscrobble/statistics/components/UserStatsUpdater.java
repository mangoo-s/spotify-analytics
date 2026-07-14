package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.UserArtistStatsEntity;
import com.example.spotifyscrobble.statistics.entities.UserArtistStatsId;
import com.example.spotifyscrobble.statistics.entities.UserTrackStatsEntity;
import com.example.spotifyscrobble.statistics.entities.UserTrackStatsId;
import com.example.spotifyscrobble.statistics.repositories.UserArtistStatsRepository;
import com.example.spotifyscrobble.statistics.repositories.UserTrackStatsRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UserStatsUpdater {
    private final UserTrackStatsRepository userTrackStatsRepository;
    private final UserArtistStatsRepository userArtistStatsRepo;

    public UserStatsUpdater(UserTrackStatsRepository userTrackStatsRepository, UserArtistStatsRepository userArtistStatsRepo) {
        this.userTrackStatsRepository = userTrackStatsRepository;
        this.userArtistStatsRepo = userArtistStatsRepo;
    }

    @Transactional
    public void recordArtistPlay(Long userId, Long artistId, String artistName){
        UserArtistStatsId id = new UserArtistStatsId(userId, artistId);
        userArtistStatsRepo.findById(id).ifPresentOrElse(
                stats -> userArtistStatsRepo.incrementTotalPlays(id),
                () -> userArtistStatsRepo.save(new UserArtistStatsEntity(id, artistName))
        );
    }
    @Transactional
    public void recordTrackPlay(Long userId, Long trackId, String trackName, String artistName){
        UserTrackStatsId id = new UserTrackStatsId(userId, trackId);
        userTrackStatsRepository.findById(id).ifPresentOrElse(
                stats -> userTrackStatsRepository.incrementTotalPlays(id),
                () -> userTrackStatsRepository.save(new UserTrackStatsEntity(id, artistName, trackName))
        );
    }

}
