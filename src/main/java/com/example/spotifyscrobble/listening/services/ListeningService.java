package com.example.spotifyscrobble.listening.services;

import com.example.spotifyscrobble.listening.TrackListenedEvent;
import com.example.spotifyscrobble.listening.entities.ListenEventEntity;
import com.example.spotifyscrobble.listening.responses.ListeningHistoryResponse;
import com.example.spotifyscrobble.listening.repositories.ListeningHistoryRepository;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.users.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Slf4j
public class ListeningService {
    private final ApplicationEventPublisher events;
    private final ListeningHistoryRepository listeningHistoryRepo;

    public ListeningService(ApplicationEventPublisher events, ListeningHistoryRepository listeningHistoryRepo){
        this.events = events;
        this.listeningHistoryRepo = listeningHistoryRepo;
    }

    @Transactional
    public void processTrackListen(TrackListenedEvent event) {
        ListenEventEntity entity = new ListenEventEntity(event.userId(), event.username(), event.artistName(),event.trackName(), event.spotifyTrackId(), event.spotifyArtistId(), event.playedAt(), event.duration());
        listeningHistoryRepo.save(entity);

        log.info("User {}'s track event is being published.", event.userId());
        events.publishEvent(event);
    }

    public CustomPageResponse<ListeningHistoryResponse> getUserListeningHistory(String username, Pageable p){
        Page<ListeningHistoryResponse> result = listeningHistoryRepo.findAllByUsername(username, p)
                .map(entity -> new ListeningHistoryResponse(
                        entity.getArtistName(),
                        entity.getTrackName(),
                        entity.getSpotifyArtistId(),
                        entity.getSpotifyTrackId(),
                        entity.getPlayedAt()
                        )
                );
        return new CustomPageResponse<>(result);
    }
}
