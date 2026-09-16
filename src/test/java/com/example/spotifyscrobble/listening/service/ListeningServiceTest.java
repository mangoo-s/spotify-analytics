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
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ListeningServiceTest {

    @Mock
    private ListeningHistoryRepository listeningHistoryRepo;

    @Mock
    private ApplicationEventPublisher events;

    @Mock
    private UsersApi usersApi;

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

        verify(events).publishEvent(event);
    }

    @Test
    void getUserListeningHistory_MapsEntitiesToResponseCorrectly(){
        String username = "testUser";
        Pageable pageable = PageRequest.of(0, 10);
        ListenEventEntity entity = new ListenEventEntity(
                UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"),
                username,
                "Bladee",
                "unreal",
                "spotifyTrackId",
                "spotifyArtistId",
                Instant.now(),
                1L
        );
        Page<ListenEventEntity> page = new PageImpl<>(List.of(entity), pageable, 1);
        when(listeningHistoryRepo.findAllByUsername(username, pageable)).thenReturn(page);

        CustomPageResponse<ListeningHistoryResponse> result = service.getUserListeningHistory(username, pageable);

        assertThat(result.content()).isNotNull();
        ListeningHistoryResponse dto = result.content().get(0);

        assertThat(dto.artistName()).isEqualTo("Bladee");
        assertThat(dto.spotifyArtistId()).isEqualTo("spotifyArtistId");
        assertThat(dto.trackName()).isEqualTo("unreal");
        assertThat(dto.spotifyTrackId()).isEqualTo("spotifyTrackId");

        verify(listeningHistoryRepo).findAllByUsername(username, pageable);
    }

    @Test
    void getUserListeningHistory_ReturnsNoContentWhenUserHasNoPlays(){
        String username = "testUser";
        Pageable pageable = PageRequest.of(0, 10);
        when(listeningHistoryRepo.findAllByUsername(username, pageable)).thenReturn(Page.empty());

        CustomPageResponse<ListeningHistoryResponse> res = service.getUserListeningHistory(username, pageable);

        assertThat(res.content().isEmpty()).isTrue();
    }

    @Test
    void getUserListeningHistory_CheckIfExceptionIsThrownWhenUserDoesNotExistAndNoPageableIsReturned(){
        when(usersApi.checkIfUserExistsByUsername(any())).thenReturn(false);
        Pageable pageable = PageRequest.of(0, 10);

        assertThrows(UsernameNotFoundException.class, () -> service.getUserListeningHistory("testUser", pageable));

        verify(listeningHistoryRepo, never()).findAllByUsername(any(), any());
    }
}
