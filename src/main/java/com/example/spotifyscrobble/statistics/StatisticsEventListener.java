package com.example.spotifyscrobble.statistics;

import com.example.spotifyscrobble.catalog.ArtistCreatedEvent;
import com.example.spotifyscrobble.listening.TrackListenedEvent;
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
    public void onTrackListenedEvent(TrackListenedEvent event){
        log.info("TrackListenedEvent has been received by StatisticsEventListener.");
        statsService.handleTrackListenedEvent(event);
        log.info("TrackListenedEvent has been completed in StatisticsEventListener");

    }

    @ApplicationModuleListener
    public void onArtistCreatedEvent(ArtistCreatedEvent event){
        log.info("ArtistCreatedEvent has been received by StatisticsEventListener.");
    }
}
