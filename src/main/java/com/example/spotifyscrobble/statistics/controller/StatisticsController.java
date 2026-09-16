package com.example.spotifyscrobble.statistics.controller;

import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.statistics.UserTopArtistResponse;
import com.example.spotifyscrobble.statistics.UserTopTracksResponse;
import com.example.spotifyscrobble.statistics.services.StatisticsService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/statistics")
public class StatisticsController {
    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/{username}/top-tracks")
    public ResponseEntity<CustomPageResponse<UserTopTracksResponse>> getUserTopTracks(@PathVariable String username, @PageableDefault(size = 10, page = 0) Pageable page){
        CustomPageResponse<UserTopTracksResponse> response = statisticsService.getUsersTopTracks(username, page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{username}/top-artists")
    public ResponseEntity<CustomPageResponse<UserTopArtistResponse>> getUserTopArtists(@PathVariable String username, @PageableDefault(size = 10, page = 0) Pageable page){
        CustomPageResponse<UserTopArtistResponse> response = statisticsService.getUsersTopArtists(username, page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
