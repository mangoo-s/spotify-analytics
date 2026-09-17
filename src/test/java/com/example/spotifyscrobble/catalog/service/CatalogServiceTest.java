package com.example.spotifyscrobble.catalog.service;

import com.example.spotifyscrobble.catalog.*;
import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import com.example.spotifyscrobble.catalog.entity.TrackEntity;
import com.example.spotifyscrobble.catalog.internalDto.*;
import com.example.spotifyscrobble.catalog.repository.ArtistRepository;
import com.example.spotifyscrobble.catalog.repository.TrackRepository;
import com.example.spotifyscrobble.shared.ArtistAlreadyExists;
import com.example.spotifyscrobble.shared.ArtistNotFoundException;
import com.example.spotifyscrobble.shared.TrackAlreadyExistsException;
import com.example.spotifyscrobble.shared.TrackNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CatalogServiceTest {

    @Mock
    private ArtistRepository artistRepo;

    @Mock
    private TrackRepository trackRepo;

    @Mock
    private ApplicationEventPublisher events;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    void createArtist_ThrowsExceptionIfArtistDoesNotExist(){
        when(artistRepo.existsBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(true);
        assertThrows(ArtistAlreadyExists.class, () -> catalogService.createArtist(new ArtistCreatedRequest("Bladee", "test")));
        verify(artistRepo, never()).save(any());

    }

    @Test
    void createArtist_ArtistGetsCreatedWhenItDoesntExist(){
        when(artistRepo.existsBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(false);
        when(artistRepo.save(any(ArtistEntity.class))).thenAnswer(InvocationOnMock -> InvocationOnMock.getArgument(0));

        catalogService.createArtist(new ArtistCreatedRequest("Bladee", "test"));

        ArgumentCaptor<ArtistEntity> captor = ArgumentCaptor.forClass(ArtistEntity.class);
        verify(artistRepo).save(captor.capture());
        assertThat(captor.getValue().getSpotifyId()).isEqualTo("test");
        assertThat(captor.getValue().getName()).isEqualTo("Bladee");
        verify(artistRepo, times(1)).save(any());
    }

    @Test
    void createArtist_returnsCorrectResponse(){
        when(artistRepo.existsBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(false);
        when(artistRepo.save(any(ArtistEntity.class))).thenAnswer(InvocationOnMock -> InvocationOnMock.getArgument(0));

        ArtistCreatedResponse response = catalogService.createArtist(new ArtistCreatedRequest("Bladee", "test"));

        assertThat(response.name()).isEqualTo("Bladee");
        assertThat(response.spotifyId()).isEqualTo("test");

    }

    @Test
    void createArtist_CreateArtistPublishesEvent(){
        when(artistRepo.existsBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(false);
        when(artistRepo.save(any(ArtistEntity.class))).thenAnswer(InvocationOnMock -> InvocationOnMock.getArgument(0));

        catalogService.createArtist(new ArtistCreatedRequest("Bladee", "test"));

        ArgumentCaptor<ArtistCreatedEvent> captor = ArgumentCaptor.forClass(ArtistCreatedEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().artist().getName()).isEqualTo("Bladee");
        assertThat(captor.getValue().artist().getSpotifyId()).isEqualTo("test");

    }

    @Test
    void createArtist_EnsureEventIsNotPublishedWhenArtistExists(){
        when(artistRepo.existsBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(true);
        assertThrows(ArtistAlreadyExists.class, () -> catalogService.createArtist(new ArtistCreatedRequest("Bladee", "test")));

        verify(events, never()).publishEvent(any());
    }

    @Test
    void createTrack_EnsureTrackDoesNotGetCreatedIfArtistDoesNotExist(){
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test", 1L);
        when(artistRepo.findBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(Optional.empty());
        assertThrows(ArtistNotFoundException.class, () -> catalogService.createTrack(req));

        verify(trackRepo, never()).save(any());
        verify(trackRepo, never()).existsBySpotifyIdAndDeletedAtIsNull(any());
    }

    @Test
    void createTrack_EnsureTrackGetsCreatedWhenArtistExistsAndTrackDoesNotExist(){
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test", 1L);
        ArtistEntity artist = new ArtistEntity("Bladee", "test");
        when(artistRepo.findBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(Optional.of(artist));
        when(trackRepo.save(any(TrackEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trackRepo.existsBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(false);

        TrackCreatedResponse res = catalogService.createTrack(req);

        verify(trackRepo).save(any());
        assertThat(res.title()).isEqualTo("unreal");
        assertThat(res.duration()).isEqualTo(1L);
        assertThat(res.spotifyId()).isEqualTo("test");
        assertThat(res.artist()).isEqualTo("Bladee");
    }

    @Test
    void createTrack_EnsureTrackDoesNotGetCreatedWhenArtistAndTrackExists(){
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test", 1L);
        ArtistEntity artist = new ArtistEntity("Bladee", "test");

        when(artistRepo.findBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(Optional.of(artist));
        when(trackRepo.existsBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(true);

        assertThrows(TrackAlreadyExistsException.class, () -> catalogService.createTrack(req));

        verify(trackRepo, never()).save(any());
    }

    @Test
    void createTrack_EnsureTrackEventIsPublishedWhenTrackGetsCreated(){
        ArtistEntity artist = new ArtistEntity("Bladee", "test1");
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test1", 1L);
        when(artistRepo.findBySpotifyIdAndDeletedAtIsNull("test1")).thenReturn(Optional.of(artist));
        when(trackRepo.existsBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(false);
        when(trackRepo.save(any())).thenAnswer(Invocation -> Invocation.getArgument(0));

        TrackCreatedResponse response = catalogService.createTrack(req);
        ArgumentCaptor<TrackCreatedEvent> captor = ArgumentCaptor.forClass(TrackCreatedEvent.class);

        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().track().getTitle()).isEqualTo("unreal");
        assertThat(captor.getValue().track().getSpotifyId()).isEqualTo("test");
        assertThat(captor.getValue().track().getArtist().getName()).isEqualTo("Bladee");
        assertThat(captor.getValue().track().getArtist().getSpotifyId()).isEqualTo("test1");
    }

    @Test
    void createTrack_EnsureTrackEventIsNotPublishedWhenArtistDoesntExist(){
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test1", 1L);
        when(artistRepo.findBySpotifyIdAndDeletedAtIsNull(any())).thenReturn(Optional.empty());
        assertThrows(ArtistNotFoundException.class, () -> catalogService.createTrack(req));

        verify(events, never()).publishEvent(any());
    }

    @Test
    void createTrack_EnsureTrackEventIsNotPublishedWhenTrackExits(){
        ArtistEntity artist = new ArtistEntity("Bladee", "test1");
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test1", 1L);

        when(artistRepo.findBySpotifyIdAndDeletedAtIsNull("test1")).thenReturn(Optional.of(artist));
        when(trackRepo.existsBySpotifyIdAndDeletedAtIsNull("test")).thenReturn(true);

        assertThrows(TrackAlreadyExistsException.class, () -> catalogService.createTrack(req));

        verify(events, never()).publishEvent(any());
    }

    @Test
    void getArtistAndTrackByTrackId_verifyIfCorrectResponseIsReturned(){
        ArtistEntity artist = new ArtistEntity("Bladee", "test");
        TrackEntity track = new TrackEntity("spotifyId", artist, "unreal", 1L);
        when(trackRepo.findByTrackIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(track));

        GetArtistAndTrackbyTrackIdDto res = catalogService.getArtistAndTrackByTrackId(1L);

        assertThat(res.artistName()).isEqualTo("Bladee");
        assertThat(res.trackName()).isEqualTo("unreal");
    }

    @Test
    void getArtistAndTrackByTrackId_throwsExceptionWhenTrackNotFound(){
        when(trackRepo.findByTrackIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThrows(TrackNotFoundException.class, () -> catalogService.getArtistAndTrackByTrackId(1L));
    }

    @Test
    void getArtistNameById_verifyThatNameIsReturnedIfArtistExists(){
        ArtistEntity artist = new ArtistEntity("Bladee", "spotifyId");
        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(artist));

        String res = catalogService.getArtistNameById(1L);

        assertThat(res).isEqualTo("Bladee");
    }

    @Test
    void getArtistNameById_throwsExceptionWhenArtistIsNotFound(){
        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> catalogService.getArtistNameById(1L));
    }

    @Test
    void getArtist_ReturnsArtistWhenArtistExists(){
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(artist));

        GetArtistResponse response = catalogService.getArtist(1L);

        assertThat(response.name()).isEqualTo("artist");
        assertThat(response.spotifyId()).isEqualTo("artistSpotifyId");
    }

    @Test
    void getArtist_ThrowsErrorWhenArtistDoesntExist(){
        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> catalogService.getArtist(1L));

    }

    @Test
    void getTrack_ReturnsTrackWhenTrackExists(){
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        TrackEntity track = new TrackEntity("trackSpotifyId", artist, "title", 3600L);
        when(trackRepo.findByTrackIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(track));

        GetTrackResponse response = catalogService.getTrack(1L);

        assertThat(response.trackName()).isEqualTo("title");
        assertThat(response.trackSpotifyId()).isEqualTo("trackSpotifyId");
        assertThat(response.length()).isEqualTo(3600L);
        assertThat(response.artistName()).isEqualTo("artist");
        assertThat(response.artistSpotifyId()).isEqualTo("artistSpotifyId");
    }

    @Test
    void deleteArtist_DeletesArtistWhenItExistsAndPublishesEvent(){
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(artist));

        catalogService.deleteArtist(1L);

        ArgumentCaptor<ArtistDeletedEvent> captor = ArgumentCaptor.forClass(ArtistDeletedEvent.class);
        verify(artistRepo, times(1)).save(artist);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().id()).isEqualTo(1L);
        assertThat(artist.getDeletedAt()).isNotNull();
    }

    @Test
    void deleteArtist_DoesNotDeleteArtistWhenItDoesNotExist(){
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> catalogService.deleteArtist(1L));

        verify(artistRepo, never()).save(any());
        verify(events, never()).publishEvent(any());
        assertThat(artist.getDeletedAt()).isNull();
    }

    @Test
    void deleteTrack_DeletesTrackWhenItExistsAndPublishesEvent(){
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        TrackEntity track = new TrackEntity("trackSpotifyId", artist, "title", 3600L);
        when(trackRepo.findByTrackIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(track));

        catalogService.deleteTrack(1L);

        ArgumentCaptor<TrackDeletedEvent> captor = ArgumentCaptor.forClass(TrackDeletedEvent.class);
        verify(trackRepo, times(1)).save(track);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().trackId()).isEqualTo(1L);
        assertThat(track.getDeletedAt()).isNotNull();

    }

    @Test
    void deleteTrack_DoesNotDeleteTrackWhenTrackDoesNotExist(){
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        TrackEntity track = new TrackEntity("trackSpotifyId", artist, "title", 3600L);
        when(trackRepo.findByTrackIdAndDeletedAtIsNull(1L)).thenReturn(Optional.empty());

        assertThrows(TrackNotFoundException.class, () -> catalogService.deleteTrack(1L));

        verify(trackRepo, never()).save(any());
        verify(events, never()).publishEvent(any());
        assertThat(track.getDeletedAt()).isNull();
    }

    @Test
    void updateArtist_UpdatesArtistWhenGivenValidFields(){
        UpdateArtistRequest request = new UpdateArtistRequest("updatedArtist", "updatedArtistSpotifyId");
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");

        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(artist));
        when(artistRepo.existsBySpotifyIdAndDeletedAtIsNull("updatedArtistSpotifyId")).thenReturn(false);

        catalogService.updateArtist(1L, request);

        verify(artistRepo).save(artist);
        assertThat(artist.getSpotifyId()).isEqualTo("updatedArtistSpotifyId");
        assertThat(artist.getName()).isEqualTo("updatedArtist");

    }

    @Test
    void updateArtist_UpdatesArtistWhenGivenPartialFields(){
        UpdateArtistRequest request = new UpdateArtistRequest("", "updatedArtistSpotifyId");
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");

        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(artist));
        when(artistRepo.existsBySpotifyIdAndDeletedAtIsNull("updatedArtistSpotifyId")).thenReturn(false);

        catalogService.updateArtist(1L, request);

        verify(artistRepo).save(artist);
        assertThat(artist.getSpotifyId()).isEqualTo("updatedArtistSpotifyId");
        assertThat(artist.getName()).isEqualTo("artist");

    }

    @Test
    void updateArtist_DoesNotUpdateArtistWhenGivenNoFields(){
        UpdateArtistRequest request = new UpdateArtistRequest("", "");
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");

        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(artist));

        catalogService.updateArtist(1L, request);

        verify(artistRepo).save(artist);
        assertThat(artist.getName()).isEqualTo("artist");
        assertThat(artist.getSpotifyId()).isEqualTo("artistSpotifyId");
    }

    @Test
    void updateArtist_ThrowsErrorWhenGivenSpotifyIdAlreadyExistsAsSpotifyIdsNeedToBeUnique(){
        UpdateArtistRequest request = new UpdateArtistRequest("", "artistSpotifyId");
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");

        when(artistRepo.findByArtistIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(artist));
        when(artistRepo.existsBySpotifyIdAndDeletedAtIsNull("artistSpotifyId")).thenReturn(true);

        assertThrows(ArtistAlreadyExists.class, () -> catalogService.updateArtist(1L, request));

        verify(artistRepo, never()).save(any());

    }

    @Test
    void updateTrack_UpdatesTrackWhenAllGivenFieldsAreValid(){
        UpdateTrackRequest request = new UpdateTrackRequest("updatedTrack", "updatedTrackSpotifyId", 3500L);
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        TrackEntity track = new TrackEntity("trackSpotifyId", artist, "title", 3600L);

        when(trackRepo.findByTrackIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(track));
        when(trackRepo.existsBySpotifyIdAndDeletedAtIsNull("updatedTrackSpotifyId")).thenReturn(false);

        catalogService.updateTrack(1L, request);

        verify(trackRepo).save(track);
        assertThat(track.getSpotifyId()).isEqualTo("updatedTrackSpotifyId");
        assertThat(track.getTitle()).isEqualTo("updatedTrack");
        assertThat(track.getDuration()).isEqualTo(3500L);
        assertThat(track.getArtist()).isEqualTo(artist);

    }

    @Test
    void updateTrack_UpdatesTrackWhenPartialFieldsAreGiven(){
        UpdateTrackRequest request = new UpdateTrackRequest("", "updatedTrackSpotifyId", null);
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        TrackEntity track = new TrackEntity("trackSpotifyId", artist, "title", 3600L);

        when(trackRepo.findByTrackIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(track));
        when(trackRepo.existsBySpotifyIdAndDeletedAtIsNull("updatedTrackSpotifyId")).thenReturn(false);

        catalogService.updateTrack(1L, request);

        verify(trackRepo).save(track);
        assertThat(track.getSpotifyId()).isEqualTo("updatedTrackSpotifyId");
        assertThat(track.getTitle()).isEqualTo("title");
        assertThat(track.getDuration()).isEqualTo(3600L);
        assertThat(track.getArtist()).isEqualTo(artist);
    }

    @Test
    void updateTrack_ThrowsErrorWhenTrackWithSameSpotifyIdGivenInRequestIsPresent(){
        UpdateTrackRequest request = new UpdateTrackRequest("", "updatedTrackSpotifyId", 3600L);
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        TrackEntity track = new TrackEntity("trackSpotifyId", artist, "title", 3600L);

        when(trackRepo.findByTrackIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(track));
        when(trackRepo.existsBySpotifyIdAndDeletedAtIsNull("updatedTrackSpotifyId")).thenReturn(true);

        assertThrows(TrackAlreadyExistsException.class, () -> catalogService.updateTrack(1L, request));

        verify(trackRepo, never()).save(any());
        assertThat(track.getSpotifyId()).isEqualTo("trackSpotifyId");
        assertThat(track.getTitle()).isEqualTo("title");
        assertThat(track.getDuration()).isEqualTo(3600L);
        assertThat(track.getArtist()).isEqualTo(artist);

    }

    @Test
    void updateTrack_DoesNotUpdateAnythingWhenNoFieldsAreGiven(){
        UpdateTrackRequest request = new UpdateTrackRequest("", "", null);
        ArtistEntity artist = new ArtistEntity("artist", "artistSpotifyId");
        TrackEntity track = new TrackEntity("trackSpotifyId", artist, "title", 3600L);

        when(trackRepo.findByTrackIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(track));

        catalogService.updateTrack(1L, request);

        verify(trackRepo).save(track);
        assertThat(track.getSpotifyId()).isEqualTo("trackSpotifyId");
        assertThat(track.getTitle()).isEqualTo("title");
        assertThat(track.getDuration()).isEqualTo(3600L);
        assertThat(track.getArtist()).isEqualTo(artist);

    }

}
