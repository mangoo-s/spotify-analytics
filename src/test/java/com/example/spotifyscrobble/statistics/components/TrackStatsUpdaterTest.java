package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.TrackStatsEntity;
import com.example.spotifyscrobble.statistics.repositories.TrackStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrackStatsUpdaterTest { //Need to check for race conditions
    @Mock
    private TrackStatsRepository trackStatsRepo;

    @InjectMocks
    private TrackStatsUpdater trackStatsUpdater;

    @Test
    void incrementTrackPlayIgnoringBranch(){
        when(trackStatsRepo.existsById(1L)).thenReturn(true);

        trackStatsUpdater.recordPlay(1L, "Idioteque", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"));

        verify(trackStatsRepo).incrementTrackPlays(1L);
        verify(trackStatsRepo, never()).save(any());
    }

    @Test
    void ensureTrackIsCreatedIfItDoesntExist(){
        when(trackStatsRepo.existsById(1L)).thenReturn(false);
        ArgumentCaptor<TrackStatsEntity> captor = ArgumentCaptor.forClass(TrackStatsEntity.class);

        trackStatsUpdater.recordPlay(1L, "Idioteque", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"));

        verify(trackStatsRepo).save(captor.capture());
        assertThat(captor.getValue().getTrackId()).isEqualTo(1L);
        assertThat(captor.getValue().getTitle()).isEqualTo("Idioteque");

        verify(trackStatsRepo).incrementTrackPlays(1L);
    }

    @Test
    void shouldNotIncrementIfSaveFails(){
        when(trackStatsRepo.existsById(1L)).thenReturn(false);
        when(trackStatsRepo.save(any())).thenThrow(new DataIntegrityViolationException("dup"));

        assertThrows(DataIntegrityViolationException.class, () ->
                trackStatsUpdater.recordPlay(1L, "Idioteque", UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b")));

        verify(trackStatsRepo, never()).incrementTrackPlays(anyLong());
    }
}
