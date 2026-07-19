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
    private final UserTrackStatsRepository userTrackStatsRepo;
    private final UserArtistStatsRepository userArtistStatsRepo;

    public UserStatsUpdater(UserTrackStatsRepository userTrackStatsRepo, UserArtistStatsRepository userArtistStatsRepo) {
        this.userTrackStatsRepo = userTrackStatsRepo;
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
        userTrackStatsRepo.findById(id).ifPresentOrElse(
                stats -> userTrackStatsRepo.incrementTotalPlays(id),
                () -> userTrackStatsRepo.save(new UserTrackStatsEntity(id, artistName, trackName))
        );
    }

}
