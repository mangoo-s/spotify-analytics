package listening;

import Catalog.CatalogEntriesExistResponse;
import Catalog.CatalogService;
import Catalog.entity.ArtistEntity;
import Catalog.entity.TrackEntity;
import listening.internal.ListenEventEntity;
import listening.internal.EventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListeningService {
    private final ApplicationEventPublisher events;
    private final EventRepository eventRepo;
    private final CatalogService catalogService;
    private static final Logger log = LoggerFactory.getLogger(ListeningService.class);

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
