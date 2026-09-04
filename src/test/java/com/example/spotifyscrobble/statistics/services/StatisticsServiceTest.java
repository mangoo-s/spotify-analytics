package com.example.spotifyscrobble.statistics.services;

import com.example.spotifyscrobble.catalog.CatalogEntriesResolvedEvent;
import com.example.spotifyscrobble.statistics.components.ArtistStatsUpdater;
import com.example.spotifyscrobble.statistics.components.TrackStatsUpdater;
import com.example.spotifyscrobble.statistics.components.UserStatsUpdater;
import com.example.spotifyscrobble.statistics.repositories.UserArtistStatsRepository;
import com.example.spotifyscrobble.statistics.repositories.UserTrackStatsRepository;
import com.example.spotifyscrobble.users.UsersApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private UserArtistStatsRepository userArtistStatsRepo;

    @Mock
    private UserTrackStatsRepository userTrackStatsRepo;

    @Mock
    private UserStatsUpdater userStatsUpdater;

    @Mock
    private UsersApi usersApi;

    @Mock
    private ArtistStatsUpdater artistStatsUpdater;

    @Mock
    private TrackStatsUpdater trackStatsUpdater;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void handleCatalogEntriesResolvedEventsToEnsureProperAggregationOfStatistics() {
        var event = new CatalogEntriesResolvedEvent(
                UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"),
                1L,
                1L,
                "psycho",
                "Bladee"
        );

        statisticsService.handleTrackListenedEvent(event);
        verify(artistStatsUpdater).recordListen(1L, "Bladee", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"));
        verify(trackStatsUpdater).recordPlay(1L, "psycho", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"));
        verify(userStatsUpdater).recordArtistPlay(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 1L, "Bladee");
        verify(userStatsUpdater).recordTrackPlay(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 1L, "psycho", "Bladee");
    }

    @Test
    void handleCatalogEntriesEvent_doesNotTouchUnrelatedRepositories() {
        var event = new CatalogEntriesResolvedEvent(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 2L, 10L, "Idioteque", "Radiohead");

        statisticsService.handleTrackListenedEvent(event);

        verifyNoInteractions(userArtistStatsRepo, userTrackStatsRepo, usersApi);
    }

}
