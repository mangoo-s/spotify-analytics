package com.example.spotifyscrobble.listening.controller;

import com.example.spotifyscrobble.listening.responses.ListeningHistoryResponse;
import com.example.spotifyscrobble.listening.services.ListeningService;
import com.example.spotifyscrobble.listening.TrackListenedEvent;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;

@RestController
public class ListenController {
    private final ListeningService listeningService;

    public ListenController(ListeningService listeningService) {
        this.listeningService = listeningService;
    }

    @GetMapping("/users/{username}/history")
    public ResponseEntity<CustomPageResponse<ListeningHistoryResponse>> getListeningHistory(@PathVariable String username, Pageable p){
        CustomPageResponse<ListeningHistoryResponse> response = listeningService.getUserListeningHistory(username, p);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        TrackListenedEvent tst = new TrackListenedEvent(
                UUID.fromString("d65b26f8-94ef-498e-b617-b51e17ce56e5"),
                "test",
                2L,
                1L,
                Instant.now(),
                "Drake",
                "Jumpman"
        );
        listeningService.processTrackListen(tst);
        return ResponseEntity.status(HttpStatus.OK).body("tested");
    }
}
