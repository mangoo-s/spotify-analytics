package com.example.spotifyscrobble.catalog;

import com.example.spotifyscrobble.catalog.Dto.ArtistCreatedRequest;
import com.example.spotifyscrobble.catalog.Dto.ArtistCreatedResponse;
import com.example.spotifyscrobble.catalog.Dto.TrackCreatedRequest;
import com.example.spotifyscrobble.catalog.Dto.TrackCreatedResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController("/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @PostMapping("/artist")
    public ResponseEntity<ArtistCreatedResponse> createNewArtist(@RequestBody @Valid ArtistCreatedRequest artistCreatedRequest){
        ArtistCreatedResponse response = catalogService.createArtist(artistCreatedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/track")
    public ResponseEntity<TrackCreatedResponse> createNewTrack(@Valid @RequestBody TrackCreatedRequest trackCreatedRequest){
        TrackCreatedResponse response = catalogService.createTrack(trackCreatedRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
