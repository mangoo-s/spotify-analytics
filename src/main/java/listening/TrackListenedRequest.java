package listening;

import java.time.Instant;

public record TrackListenedRequest(
        Long userId,
        Long spotifyTrackId,
        Long spotifyArtistId,
        Instant playedAt,
        String artistName,
        String trackName
) {
}
