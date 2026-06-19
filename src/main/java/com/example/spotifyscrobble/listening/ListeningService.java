package com.example.spotifyscrobble.listening;

import com.example.spotifyscrobble.catalog.CatalogEntriesExistResponse;
import com.example.spotifyscrobble.catalog.CatalogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ListeningService {
    private final ApplicationEventPublisher events;
    private final EventRepository eventRepo;
    private final CatalogService catalogService;

    public ListeningService(ApplicationEventPublisher events, EventRepository eventRepo, CatalogService catalogService){
        this.events = events;
        this.eventRepo = eventRepo;
        this.catalogService = catalogService;
    }

    @Transactional
    public void processTrackListen(TrackListenedRequest trackListenedRequest){
        ListenEventEntity entity = new ListenEventEntity(trackListenedRequest.userId(), trackListenedRequest.spotifyTrackId(), trackListenedRequest.spotifyArtistId(), trackListenedRequest.playedAt());
        eventRepo.save(entity);

        CatalogEntriesExistResponse values = catalogService.ensureCatalogEntriesExist(trackListenedRequest);
        TrackListenedEvent event = new TrackListenedEvent(
                trackListenedRequest.userId(),
                values.artist(),
                values.track(),
                trackListenedRequest.playedAt()
        );
        log.info("User {}'s track event is being published.", trackListenedRequest.userId());
        events.publishEvent(event);
    }
}
