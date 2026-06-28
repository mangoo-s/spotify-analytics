package com.example.spotifyscrobble.catalog.eventlistener;

import com.example.spotifyscrobble.catalog.CatalogEntriesResolvedEvent;
import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import com.example.spotifyscrobble.catalog.entity.TrackEntity;
import com.example.spotifyscrobble.catalog.repository.ArtistRepository;
import com.example.spotifyscrobble.catalog.repository.TrackRepository;
import com.example.spotifyscrobble.listening.TrackListenedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CatalogEventListener {
    private final ArtistRepository artistRepo;
    private final TrackRepository trackRepo;
    private final ApplicationEventPublisher events;

    public CatalogEventListener(ArtistRepository artistRepo, TrackRepository trackRepo, ApplicationEventPublisher events) {
        this.artistRepo = artistRepo;
        this.trackRepo = trackRepo;
        this.events = events;
    }

    @ApplicationModuleListener
    public void onTrackListenedEvent(TrackListenedEvent event){
        log.info("TrackListenedEvent has been received by StatisticsEventListener.");
        ArtistEntity artist = artistRepo.findBySpotifyId(event.spotifyArtistId()).orElseGet(
                () -> artistRepo.save(new ArtistEntity(event.artistName(), event.spotifyArtistId()))
        );

        TrackEntity track = trackRepo.findBySpotifyId(event.spotifyTrackId()).orElseGet(
                () -> trackRepo.save(new TrackEntity(event.spotifyTrackId(), artist, event.trackName(), 0L))
        );
        log.info("TrackListenedEvent has been Completed by StatisticsEventListener. Now publishing CatalogEntriesResolvedEvent.");
        events.publishEvent(new CatalogEntriesResolvedEvent(event.userId(), artist.getArtistId(), track.getTrackId()));
    }
}
