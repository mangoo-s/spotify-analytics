package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.ArtistListenerEntity;
import com.example.spotifyscrobble.statistics.entities.TrackListenerEntity;
import com.example.spotifyscrobble.statistics.repositories.ArtistListenerRepository;
import com.example.spotifyscrobble.statistics.repositories.TrackListenerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class IsExistingListenerTest {

    @Mock
    private ArtistListenerRepository artistListenerRepo;

    @Mock
    private TrackListenerRepository trackListenerRepo;

    @InjectMocks
    private IsExistingListener isExistingListener;

    private final UUID userId = UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b");

    @Test
    void isExistingTrackListener_ifListenerDoesntExistEnsureThatItIsCreated(){
        when(trackListenerRepo.existsByTrackIdAndUserId(1L, userId)).thenReturn(false);

        boolean res = isExistingListener.isExistingTrackListener(userId, 1L);
        assertThat(res).isFalse();
        ArgumentCaptor<TrackListenerEntity> captor = ArgumentCaptor.forClass(TrackListenerEntity.class);

        verify(trackListenerRepo).save(captor.capture());
        assertThat(captor.getValue().getTrackId()).isEqualTo(1L);
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);
    }

    @Test
    void IsExistingTrackListener_IfListenerDoesExistEnsureItReturnsTrueAndDoesNotCreateNewListener(){
        when(trackListenerRepo.existsByTrackIdAndUserId(1L, userId)).thenReturn(true);

        boolean res = isExistingListener.isExistingTrackListener(userId, 1L);
        assertThat(res).isTrue();
        verify(trackListenerRepo, never()).save(any());
    }

    @Test
    void isExistingTrackListener_dataIntegrityViolationReturnsTrue(){
        when(trackListenerRepo.existsByTrackIdAndUserId(1L, userId)).thenReturn(false);
        when(trackListenerRepo.save(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

        boolean res = isExistingListener.isExistingTrackListener(userId, 1L);

        assertThat(res).isTrue();
    }

    @Test
    void isExistingArtistListener_IFListenerDoesntExistEnsureThatItIsCreated(){
        when(artistListenerRepo.existsByArtistIdAndUserId(1L, userId)).thenReturn(false);

        boolean res = isExistingListener.isExistingArtistListener(userId, 1L);
        assertThat(res).isFalse();

        ArgumentCaptor<ArtistListenerEntity> captor = ArgumentCaptor.forClass(ArtistListenerEntity.class);

        verify(artistListenerRepo).save(captor.capture());
        assertThat(captor.getValue().getArtistId()).isEqualTo(1L);
        assertThat(captor.getValue().getUserId()).isEqualTo(userId);

    }

    @Test
    void isExistingArtistListener_IfListenerDoesExistEnsureItReturnsTrueAndDoesNotCreateNewListener(){
        when(artistListenerRepo.existsByArtistIdAndUserId(1L, userId)).thenReturn(true);

        boolean res = isExistingListener.isExistingArtistListener(userId, 1L);
        assertThat(res).isTrue();
        verify(artistListenerRepo, never()).save(any());
    }

    @Test
    void isExistingArtistListener_dataIntegrityViolationReturnsTrue(){
        when(artistListenerRepo.existsByArtistIdAndUserId(1L, userId)).thenReturn(false);
        when(artistListenerRepo.save(any())).thenThrow(new DataIntegrityViolationException("duplicate"));

        boolean res = isExistingListener.isExistingArtistListener(userId, 1L);
        assertThat(res).isTrue();
    }
}
