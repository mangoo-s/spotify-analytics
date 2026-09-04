package com.example.spotifyscrobble.catalog;

public interface CatalogApi {

    GetArtistAndTrackbyTrackIdDto getArtistAndTrackByTrackId(long trackId);

    String getArtistNameById(long artistId);

}
