package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.ArtistListenerEntity;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ArtistStatsUpdaterTest {

    @Mock
    private ArtistStatsRepository artistStatsRepo;

    @Mock
    private ArtistListenerRepository artistListenerRepo;

    @InjectMocks
    private ArtistStatsUpdater artistStatsUpdater;

    @Test
    void recordListen_DoesNotCreateArtistWhenArtistAlreadyExists(){
        when(artistStatsRepo.existsById(1L)).thenReturn(true);
        when(artistListenerRepo.existsByArtistIdAndUserId(1L, 1L)).thenReturn(true);

        artistStatsUpdater.recordListen(1L, "Radiohead", 1L);

        verify(artistListenerRepo, never()).save(any());
    }

    @Test
    void recordListen_CreatesNewArtistWhenArtistDoesNotAlreadyExist(){
        when(artistStatsRepo.existsById(1L)).thenReturn(false);
        when(artistListenerRepo.existsByArtistIdAndUserId(1L, 100L)).thenReturn(true);

        artistStatsUpdater.recordListen(1L, "Radiohead", 100L);
        ArgumentCaptor<ArtistStatsEntity> captor = ArgumentCaptor.forClass(ArtistStatsEntity.class);
        verify(artistStatsRepo).save(captor.capture());
        assertThat(captor.getValue().getArtistId()).isEqualTo(1L);
        assertThat(captor.getValue().getName()).isEqualTo("RadioHead");
    }

    @Test
    void recordListen_IncrementsListenersForNewListener(){
        when(artistStatsRepo.existsById(1L)).thenReturn(true);
        when(artistListenerRepo.existsByArtistIdAndUserId(1L, 100L)).thenReturn(false);
        ArgumentCaptor<ArtistListenerEntity> captor = ArgumentCaptor.forClass(ArtistListenerEntity.class);

        artistStatsUpdater.recordListen(1L, "Radiohead", 100L);

        verify(artistListenerRepo).save(captor.capture());
        assertThat(captor.getValue().getArtistId()).isEqualTo(1L);
        assertThat(captor.getValue().getUserId()).isEqualTo(100L);
        verify(artistStatsRepo).incrementListeners(1L);
    }

    @Test
    void recordListen_DoesNotIncrementListenersForReturningListener(){
        when(artistStatsRepo.existsById(1L)).thenReturn(true);
        when(artistListenerRepo.existsByArtistIdAndUserId(1L, 100L)).thenReturn(true);

        artistStatsUpdater.recordListen(1L, "Radiohead", 100L);

        verify(artistListenerRepo, never()).save(any());
        verify(artistStatsRepo, never()).incrementListeners(anyLong());
    }

    @Test
    void recordListen_DoesNotIncrementListenersWhenRaceConditionOccursOnSave(){
        when(artistStatsRepo.existsById(1L)).thenReturn(true);
        when(artistListenerRepo.existsByArtistIdAndUserId(1L, 100L)).thenReturn(false);
        when(artistListenerRepo.save(any())).thenThrow(new DataIntegrityViolationException("Duplicate"));

        artistStatsUpdater.recordListen(1L, "Radiohead", 100L);

        verify(artistStatsRepo, never()).incrementListeners(anyLong());
    }

    @Test
    void recordListen_AlwaysIncrementsTotalPlaysRegardlessOfIfStatements(){
        when(artistStatsRepo.existsById(1L)).thenReturn(true);
        when(artistListenerRepo.existsByArtistIdAndUserId(1L, 100L)).thenReturn(true);

        artistStatsUpdater.recordListen(1L, "Radiohead", 100L);
        verify(artistStatsRepo).incrementTotalPlays(1L);
    }
}
