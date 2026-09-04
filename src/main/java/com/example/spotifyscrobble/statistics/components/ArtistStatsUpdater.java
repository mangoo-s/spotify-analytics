package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.ArtistListenerEntity;
import com.example.spotifyscrobble.statistics.entities.ArtistStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.ArtistListenerRepository;
import com.example.spotifyscrobble.statistics.repositories.ArtistStatsRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
public class ArtistStatsUpdater {
    private final ArtistStatsRepository artistStatsRepo;
    private final ArtistListenerRepository artistListenerRepo;

    public ArtistStatsUpdater(ArtistStatsRepository artistStatsRepo, ArtistListenerRepository artistListenerRepo) {
        this.artistStatsRepo = artistStatsRepo;
        this.artistListenerRepo = artistListenerRepo;
    }

    @Transactional
    public void recordListen(Long artistId, String artistName, UUID userId){ // time-of-check to time-of-use race condition here
        if(!artistStatsRepo.existsById(artistId)){
            artistStatsRepo.save(new ArtistStatsEntity(artistId, artistName));
        }

        if(!isExistingListener(userId, artistId)){
            artistStatsRepo.incrementListeners(artistId);
        }
        artistStatsRepo.incrementTotalPlays(artistId);
    }

    private boolean isExistingListener(UUID userId, Long artistId){
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
}
