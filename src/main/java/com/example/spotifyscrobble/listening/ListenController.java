package com.example.spotifyscrobble.listening;

import com.example.spotifyscrobble.shared.CustomPageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.security.auth.login.AccountNotFoundException;
import java.time.Instant;

@RestController
public class ListenController {
    private final ListeningService listeningService;

    public ListenController(ListeningService listeningService) {
        this.listeningService = listeningService;
    }

    @GetMapping("/users/{id}/history")
    public ResponseEntity<CustomPageResponse<ListeningHistoryResponse>> getListeningHistory(@PathVariable Long id, Pageable p){
        CustomPageResponse<ListeningHistoryResponse> response = listeningService.getUserListeningHistory(id, p); //N+1 query need to fix
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/test")
    public ResponseEntity<?> test() {
        TrackListenedEvent tst = new TrackListenedEvent(
                1L,
                2L,
                2L,
                Instant.now(),
                "Bladee",
                "Unreal"
        );
        listeningService.processTrackListen(tst);
        return ResponseEntity.status(HttpStatus.OK).body("tested");
    }
}
