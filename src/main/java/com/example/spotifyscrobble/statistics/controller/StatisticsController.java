package com.example.spotifyscrobble.statistics.controller;

import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.statistics.UserTopArtistResponse;
import com.example.spotifyscrobble.statistics.UserTopTracksResponse;
import com.example.spotifyscrobble.statistics.services.StatisticsService;
import org.springframework.data.domain.Pageable;
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

    @GetMapping("/{id}/top-tracks")
    public ResponseEntity<CustomPageResponse<UserTopTracksResponse>> getUserTopTracks(@PathVariable long id,
                                                                                      @RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size){
        CustomPageResponse<UserTopTracksResponse> response = statisticsService.getUsersTopTracks(id, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/{id}/top-artists")
    public ResponseEntity<CustomPageResponse<UserTopArtistResponse>> getUserTopArtists(@PathVariable long id,
                                                                                       @RequestParam(defaultValue = "0") int page,
                                                                                       @RequestParam(defaultValue = "10") int size){
        CustomPageResponse<UserTopArtistResponse> response = statisticsService.getUsersTopArtists(id, page, size);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
