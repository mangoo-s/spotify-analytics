package com.example.spotifyscrobble.listening.controller;

import com.example.spotifyscrobble.listening.responses.ListeningHistoryResponse;
import com.example.spotifyscrobble.listening.services.ListeningService;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ResponseEntity<CustomPageResponse<ListeningHistoryResponse>> getListeningHistory(
            @PathVariable String username,
            @RequestParam(required = false) String spotifyArtistId,
            @RequestParam(required = false) String spotifyTrackId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant endDate,
            @PageableDefault(size = 10, page = 0) Pageable pageable
    ){
        CustomPageResponse<ListeningHistoryResponse> response = listeningService.getUserListeningHistory(username, spotifyArtistId, spotifyTrackId, startDate, endDate, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
