package com.example.spotifyscrobble.catalog.controller;

import com.example.spotifyscrobble.catalog.internalDto.*;
import com.example.spotifyscrobble.catalog.service.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/catalog")
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

    @GetMapping("/artist/{id}")
    public ResponseEntity<GetArtistResponse> getArtist(@PathVariable long id){
        return ResponseEntity.status(HttpStatus.OK).body(catalogService.getArtist(id));
    }


    @GetMapping("/track/{id}")
    public ResponseEntity<GetTrackResponse> getTrack(@PathVariable long id){
        System.out.println("hello");
        return ResponseEntity.status(HttpStatus.OK).body(catalogService.getTrack(id));
    }

    @DeleteMapping("artist/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteArtist(@PathVariable long id){
        catalogService.deleteArtist(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/track/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTrack(@PathVariable long id){
        catalogService.deleteTrack(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/artist/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateArtist(@PathVariable long id, @RequestBody UpdateArtistRequest updateArtistRequest){
        catalogService.updateArtist(id, updateArtistRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/track/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateTrack(@PathVariable long id, @RequestBody UpdateTrackRequest updateTrackRequest){
        catalogService.updateTrack(id, updateTrackRequest);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
