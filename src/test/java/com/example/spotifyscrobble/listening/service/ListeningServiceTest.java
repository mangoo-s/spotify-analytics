package com.example.spotifyscrobble.listening.service;

import com.example.spotifyscrobble.listening.TrackListenedEvent;
import com.example.spotifyscrobble.listening.entities.ListenEventEntity;
import com.example.spotifyscrobble.listening.repositories.ListeningHistoryRepository;
import com.example.spotifyscrobble.listening.responses.ListeningHistoryResponse;
import com.example.spotifyscrobble.listening.services.ListeningService;
import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.users.UsersApi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListeningServiceTest {

    @Mock
    private ListeningHistoryRepository listeningHistoryRepo;

    @InjectMocks
    private ListeningService service;

    @Test
    void processTrackListen_SavesEntityAndPublishesEvent() {
        UUID userId = UUID.randomUUID();
        Instant playedAt = Instant.now();
        TrackListenedEvent event = new TrackListenedEvent(
                userId, "testUser", "trackSpotifyId", "artistSpotifyId",
                playedAt, "Bladee", "unreal", 1L
        );

        service.processTrackListen(event);

        ArgumentCaptor<ListenEventEntity> captor = ArgumentCaptor.forClass(ListenEventEntity.class);
        verify(listeningHistoryRepo).save(captor.capture());

        ListenEventEntity saved = captor.getValue();
        assertThat(saved.getUserId()).isEqualTo(userId);
        assertThat(saved.getArtistName()).isEqualTo("Bladee");
        assertThat(saved.getTrackName()).isEqualTo("unreal");
        assertThat(saved.getSpotifyTrackId()).isEqualTo("trackSpotifyId");
        assertThat(saved.getSpotifyArtistId()).isEqualTo("artistSpotifyId");
        assertThat(saved.getDuration()).isEqualTo(1L);

    }

    @Test
    void mapsEntityToResponseCorrectly() {
        UUID userId = UUID.randomUUID();
        ListenEventEntity entity = new ListenEventEntity(userId, "username", "artistName", "trackName", "spotifyTrackId", "spotifyArtistId", Instant.parse("2026-09-19T16:10:03.494Z"), 1L);

        Page<ListenEventEntity> mockPage = new PageImpl<>(List.of(entity), PageRequest.of(0, 10), 1);

        when(listeningHistoryRepo.findAll(any(Specification.class), any(Pageable.class)))
                .thenReturn(mockPage);

        CustomPageResponse<ListeningHistoryResponse> result = service.getUserListeningHistory(
                "hello", null, null, null, null, PageRequest.of(0, 10));

        assertEquals(1, result.content().size());
        assertEquals("artistName", result.content().get(0).artistName());
        assertEquals("trackName", result.content().get(0).trackName());
        assertEquals(1, result.totalElements());
    }

}
