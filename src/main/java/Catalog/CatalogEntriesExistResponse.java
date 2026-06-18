package Catalog;

import Catalog.entity.ArtistEntity;
import Catalog.entity.TrackEntity;

public record CatalogEntriesExistResponse(ArtistEntity artist, TrackEntity track) {
}
