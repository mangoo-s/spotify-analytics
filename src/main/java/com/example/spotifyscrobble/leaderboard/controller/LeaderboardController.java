package com.example.spotifyscrobble.leaderboard.controller;

import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardArtistEntry;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardGlobalArtistEntry;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardGlobalTrackEntry;
import com.example.spotifyscrobble.leaderboard.dtos.LeaderboardUserEntry;
import com.example.spotifyscrobble.leaderboard.service.LeaderboardService;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @GetMapping("/tracks/{trackId}")
    public ResponseEntity<CustomPageResponse<LeaderboardUserEntry>> getNTopTrackListeners(@PathVariable long trackId, @PageableDefault(size = 10, page = 0) Pageable pageable){
        CustomPageResponse<LeaderboardUserEntry> response = leaderboardService.getTopTrackListeners(trackId, pageable);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/artist/{artistId}")
    public ResponseEntity<CustomPageResponse<LeaderboardArtistEntry>> getNTopArtistListeners(@PathVariable long artistId, @PageableDefault(size = 10, page = 0) Pageable page){
        CustomPageResponse<LeaderboardArtistEntry> response = leaderboardService.getTopArtistListeners(artistId, page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    @GetMapping("/global/artists")
    public ResponseEntity<CustomPageResponse<LeaderboardGlobalArtistEntry>> getNTopGlobalArtists(@PageableDefault(size = 10, page = 0) Pageable page){
        CustomPageResponse<LeaderboardGlobalArtistEntry> response = leaderboardService.getTopGlobalArtists(page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/global/tracks")
    public ResponseEntity<CustomPageResponse<LeaderboardGlobalTrackEntry>> getNTopGlobalTracks(@PageableDefault(size = 10, page = 0) Pageable page){
        CustomPageResponse<LeaderboardGlobalTrackEntry> response = leaderboardService.getTopGlobalTracks(page);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
