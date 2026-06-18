package listening;

import Catalog.entity.ArtistEntity;
import Catalog.entity.TrackEntity;

import java.time.Instant;

public record TrackListenedEvent(Long userId, ArtistEntity artist,
                                 TrackEntity track,
                                 Instant playedAt
) {
}
