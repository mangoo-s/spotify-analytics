package com.example.spotifyscrobble.listening.eventlistener;

import com.example.spotifyscrobble.listening.TrackListenedEvent;
import com.example.spotifyscrobble.listening.services.ListeningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ListeningEventListener {
    private final ListeningService listeningService;

    public ListeningEventListener(ListeningService listeningService) {
        this.listeningService = listeningService;
    }

    @ApplicationModuleListener
    public void onTrackListenedEvent(TrackListenedEvent event){
        log.info("TrackListenedEvent has been received by ListeningEventListener");
        listeningService.processTrackListen(event);
    }

}
