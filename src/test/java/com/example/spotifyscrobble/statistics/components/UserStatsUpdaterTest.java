package com.example.spotifyscrobble.statistics.components;

import com.example.spotifyscrobble.statistics.entities.UserArtistStatsEntity;
import com.example.spotifyscrobble.statistics.entities.UserArtistStatsId;
import com.example.spotifyscrobble.statistics.entities.UserTrackStatsEntity;
import com.example.spotifyscrobble.statistics.entities.UserTrackStatsId;
import com.example.spotifyscrobble.statistics.repositories.UserArtistStatsRepository;
import com.example.spotifyscrobble.statistics.repositories.UserTrackStatsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserStatsUpdaterTest {
    @Mock
    private UserTrackStatsRepository userTrackStatsRepo;

    @Mock
    private UserArtistStatsRepository userArtistStatsRepo;

    @InjectMocks
    private UserStatsUpdater userStatsUpdater;

    //Record Artist Play
    @Test
    void recordArtistPlay_VerifyIfIncrementsIfIdExists(){
        UserArtistStatsId id = new UserArtistStatsId(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 2L);
        UserArtistStatsEntity existingStats = new UserArtistStatsEntity(id, "Radiohead");
        when(userArtistStatsRepo.findById(id)).thenReturn(Optional.of(existingStats));

        userStatsUpdater.recordArtistPlay(id.userId(), id.artistId(), "Radiohead");

        verify(userArtistStatsRepo).incrementTotalPlays(id);
        verify(userArtistStatsRepo, never()).save(any());
    }

    @Test
    void recordArtistPlay_SavesNewEntityIfIdDoesntExist(){
        UserArtistStatsId id = new UserArtistStatsId(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b") ,2L);
        ArgumentCaptor<UserArtistStatsEntity> captor = ArgumentCaptor.forClass(UserArtistStatsEntity.class);
        when(userArtistStatsRepo.findById(id)).thenReturn(Optional.empty());

        userStatsUpdater.recordArtistPlay(id.userId(), id.artistId(), "Radiohead");

        verify(userArtistStatsRepo).save(captor.capture());
        assertThat(captor.getValue().getArtistName()).isEqualTo("Radiohead");
        assertThat(captor.getValue().getId()).isEqualTo(id);

        verify(userArtistStatsRepo, never()).incrementTotalPlays(any());
    }

    @Test
    void recordArtistPlay_DoesNotIncrementIfSaveFails(){
        UserArtistStatsId id = new UserArtistStatsId(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 2L);
        when(userArtistStatsRepo.findById(id)).thenReturn(Optional.empty());
        when(userArtistStatsRepo.save(any())).thenThrow(new DataIntegrityViolationException("dup"));

        assertThrows(DataIntegrityViolationException.class, () ->
                userStatsUpdater.recordArtistPlay(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 2L, "Radiohead"));

        verify(userArtistStatsRepo, never()).incrementTotalPlays(any());
    }


    //Record Track Play
    @Test
    void recordTrackPlay_VerifyIfIncrementsIfIdExists(){
        UserTrackStatsId id = new UserTrackStatsId(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 2L);
        UserTrackStatsEntity existingStats = new UserTrackStatsEntity(id, "Radiohead", "No Surprises");
        when(userTrackStatsRepo.findById(id)).thenReturn(Optional.of(existingStats));

        userStatsUpdater.recordTrackPlay(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 2L, "No Surprises", "Radiohead");

        verify(userTrackStatsRepo).incrementTotalPlays(id);
        verify(userTrackStatsRepo, never()).save(any());
    }

    @Test
    void recordTrackPlay_SavesNewEntityIfIdDoesntExist(){
        UserTrackStatsId id = new UserTrackStatsId(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b") ,2L);
        ArgumentCaptor<UserTrackStatsEntity> captor = ArgumentCaptor.forClass(UserTrackStatsEntity.class);
        when(userTrackStatsRepo.findById(id)).thenReturn(Optional.empty());

        userStatsUpdater.recordTrackPlay(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 2L, "No Surprises", "Radiohead");

        verify(userTrackStatsRepo).save(captor.capture());
        assertThat(captor.getValue().getArtistName()).isEqualTo("Radiohead");
        assertThat(captor.getValue().getTrackName()).isEqualTo("No Surprises");
        assertThat(captor.getValue().getId()).isEqualTo(id);
        verify(userTrackStatsRepo, never()).incrementTotalPlays(any());
    }

    @Test
    void recordTrackPlay_DoesNotIncrementIfSaveFails(){
        UserTrackStatsId id = new UserTrackStatsId(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 2L);
        when(userTrackStatsRepo.findById(id)).thenReturn(Optional.empty());
        when(userTrackStatsRepo.save(any())).thenThrow(new DataIntegrityViolationException("dup"));

        assertThrows(DataIntegrityViolationException.class, () ->
                userStatsUpdater.recordTrackPlay(UUID.fromString("52a40a30-c59e-40a2-b92e-5417b0c3a31b"), 2L, "No Surprises", "Radiohead"));

        verify(userTrackStatsRepo, never()).incrementTotalPlays(any());
    }
}
