package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.ArtistListenerEntity;
import com.example.spotifyscrobble.statistics.entities.ArtistStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.ArtistListenerRepository;
import com.example.spotifyscrobble.statistics.repositories.ArtistStatsRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArtistStatsUpdater {
    private final ArtistStatsRepository artistStatsRepo;
    private final IsExistingListener isExistingListener;

    public ArtistStatsUpdater(ArtistStatsRepository artistStatsRepo, IsExistingListener isExistingListener) {
        this.artistStatsRepo = artistStatsRepo;
        this.isExistingListener = isExistingListener;
    }

    @Transactional
    public void recordListen(Long artistId, String artistName, UUID userId){ // time-of-check to time-of-use race condition here
        if(!artistStatsRepo.existsById(artistId)){
            artistStatsRepo.save(new ArtistStatsEntity(artistId, artistName));
        }

        if(!isExistingListener.isExistingArtistListener(userId, artistId)){
            artistStatsRepo.incrementListeners(artistId);
        }
        artistStatsRepo.incrementTotalPlays(artistId);
    }

}
