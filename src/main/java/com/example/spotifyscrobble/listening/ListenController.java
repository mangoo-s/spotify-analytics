package com.example.spotifyscrobble.listening;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
public class ListenController {
    private final ListeningService listeningService;

    public ListenController(ListeningService listeningService) {
        this.listeningService = listeningService;
    }


    @GetMapping("/test")
    public ResponseEntity<?> test(){
        TrackListenedRequest tst = new TrackListenedRequest(
                0L,
                1L,
                1L,
                Instant.now(),
                "Drake",
                "Jungle"
        );
        listeningService.processTrackListen(tst);
        return ResponseEntity.status(HttpStatus.OK).body("tested");
    }
}
