package com.example.spotifyscrobble.statistics.eventlistener;

import com.example.spotifyscrobble.catalog.ArtistCreatedEvent;
import com.example.spotifyscrobble.catalog.CatalogEntriesResolvedEvent;
import com.example.spotifyscrobble.statistics.services.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StatisticsEventListener { ;
    private final StatisticsService statsService;;

    public StatisticsEventListener( StatisticsService statsService){
        this.statsService = statsService;
    }

    @ApplicationModuleListener
    public void onCatalogEntriesResolvedEvent(CatalogEntriesResolvedEvent event){
        log.info("CatalogEntriesResolvedEvent has been received by StatisticsEventListener.");
        statsService.handleTrackListenedEvent(event);
        log.info("CatalogEntriesResolvedEvent has been completed in StatisticsEventListener");

    }

    @ApplicationModuleListener
    public void onArtistCreatedEvent(ArtistCreatedEvent event){
        log.info("ArtistCreatedEvent has been received by StatisticsEventListener.");
    }
}
