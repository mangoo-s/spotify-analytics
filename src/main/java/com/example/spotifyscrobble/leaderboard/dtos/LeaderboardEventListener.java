package com.example.spotifyscrobble.leaderboard.dtos;

import com.example.spotifyscrobble.catalog.CatalogEntriesResolvedEvent;
import com.example.spotifyscrobble.leaderboard.service.LeaderboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LeaderboardEventListener {

    private final LeaderboardService leaderboardService;

    public LeaderboardEventListener(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @ApplicationModuleListener
    public void onCatalogEntriesResolvedEvent(CatalogEntriesResolvedEvent event){
        log.info("CatalogEntriesResolvedEvent received by Leaderboard");
        leaderboardService.recordPlay(event.artistId(), event.trackId(), event.userId());
    }
}
