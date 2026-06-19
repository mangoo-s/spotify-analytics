package com.example.spotifyscrobble.listening;

import com.example.spotifyscrobble.catalog.CatalogEntriesExistResponse;
import com.example.spotifyscrobble.catalog.CatalogService;
import com.example.spotifyscrobble.shared.UserAlreadyExistsException;
import com.example.spotifyscrobble.users.UserEntity;
import com.example.spotifyscrobble.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.security.auth.login.AccountNotFoundException;

@Service
@Slf4j
public class ListeningService {
    private final ApplicationEventPublisher events;
    private final EventRepository eventRepo;
    private final CatalogService catalogService;
    private final UserRepository userRepo;

    public ListeningService(ApplicationEventPublisher events, EventRepository eventRepo, CatalogService catalogService, UserRepository userRepo){
        this.events = events;
        this.eventRepo = eventRepo;
        this.catalogService = catalogService;
        this.userRepo = userRepo;
    }

    @Transactional
    public void processTrackListen(TrackListenedRequest trackListenedRequest) throws AccountNotFoundException {
        ListenEventEntity entity = new ListenEventEntity(trackListenedRequest.userId(), trackListenedRequest.spotifyTrackId(), trackListenedRequest.spotifyArtistId(), trackListenedRequest.playedAt());
        eventRepo.save(entity);
        TrackListenedEvent event = new TrackListenedEvent(
                trackListenedRequest.userId(),
                trackListenedRequest.spotifyArtistId(),
                trackListenedRequest.spotifyTrackId(),
                trackListenedRequest.playedAt()
        );
        log.info("User {}'s track event is being published.", trackListenedRequest.userId());
        events.publishEvent(event);
    }
}
