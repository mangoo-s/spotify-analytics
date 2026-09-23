package com.example.spotifyscrobble.catalog;

import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import com.example.spotifyscrobble.catalog.entity.TrackEntity;
import com.example.spotifyscrobble.catalog.internalDto.ArtistCacheView;
import com.example.spotifyscrobble.catalog.internalDto.TrackCacheView;
import com.example.spotifyscrobble.catalog.service.CatalogService;
import org.springframework.stereotype.Service;

@Service
public class CatalogApi {
    private final CatalogService catalogService;


    public CatalogApi(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public GetArtistAndTrackbyTrackIdDto getArtistAndTrackByTrackId(long id){
        TrackCacheView track = catalogService.getTrack(id);
        return new GetArtistAndTrackbyTrackIdDto(track.artistName(), track.trackName());
    }

    public String getArtistNameById(long id){
        ArtistCacheView artist = catalogService.getArtist(id);
        return artist.artistName();
    }

}
