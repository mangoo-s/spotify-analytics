package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.ArtistStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.ArtistListenerRepository;
import com.example.spotifyscrobble.statistics.repositories.ArtistStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistStatsUpdaterTest {

    @Mock
    private ArtistStatsRepository artistStatsRepo;

    @InjectMocks
    private ArtistStatsUpdater artistStatsUpdater;

    @Mock
    private IsExistingListener isExistingListener;

    @Test
    void recordListen_DoesNotCreateArtistWhenArtistAlreadyExists(){
        when(artistStatsRepo.existsById(1L)).thenReturn(true);

        artistStatsUpdater.recordListen(1L, "Radiohead", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"));
    }

    @Test
    void recordListen_CreatesNewArtistWhenArtistDoesNotAlreadyExist(){
        when(artistStatsRepo.existsById(1L)).thenReturn(false);

        artistStatsUpdater.recordListen(1L, "Radiohead", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"));
        ArgumentCaptor<ArtistStatsEntity> captor = ArgumentCaptor.forClass(ArtistStatsEntity.class);
        verify(artistStatsRepo).save(captor.capture());
        assertThat(captor.getValue().getArtistId()).isEqualTo(1L);
        assertThat(captor.getValue().getName()).isEqualTo("Radiohead");
    }

    @Test
    void recordListen_IncrementsListenersForNewListener(){
        when(artistStatsRepo.existsById(1L)).thenReturn(true);
        when(isExistingListener.isExistingArtistListener(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 1L)).thenReturn(false);

        artistStatsUpdater.recordListen(1L, "Radiohead", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"));

        verify(artistStatsRepo).incrementListeners(1L);
    }

    @Test
    void recordListen_DoesNotIncrementListenersForReturningListener(){
        when(artistStatsRepo.existsById(1L)).thenReturn(true);
        when(isExistingListener.isExistingArtistListener(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 1L)).thenReturn(true);

        artistStatsUpdater.recordListen(1L, "Radiohead", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"));

        verify(artistStatsRepo, never()).incrementListeners(anyLong());
    }


    @Test
    void recordListen_AlwaysIncrementsTotalPlaysRegardlessOfIfStatements(){
        when(artistStatsRepo.existsById(1L)).thenReturn(true);

        artistStatsUpdater.recordListen(1L, "Radiohead", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"));
        verify(artistStatsRepo).incrementTotalPlays(1L);
    }
}
