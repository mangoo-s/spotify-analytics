package com.example.spotifyscrobble.catalog.service;

import com.example.spotifyscrobble.catalog.ArtistCreatedEvent;
import com.example.spotifyscrobble.catalog.GetArtistAndTrackbyTrackIdDto;
import com.example.spotifyscrobble.catalog.entity.ArtistEntity;
import com.example.spotifyscrobble.catalog.entity.TrackEntity;
import com.example.spotifyscrobble.catalog.internalDto.ArtistCreatedRequest;
import com.example.spotifyscrobble.catalog.internalDto.ArtistCreatedResponse;
import com.example.spotifyscrobble.catalog.internalDto.TrackCreatedRequest;
import com.example.spotifyscrobble.catalog.internalDto.TrackCreatedResponse;
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
        when(artistRepo.existsBySpotifyId("test")).thenReturn(true);
        assertThrows(ArtistAlreadyExists.class, () -> catalogService.createArtist(new ArtistCreatedRequest("Bladee", "test")));
        verify(artistRepo, never()).save(any());

    }

    @Test
    void createArtist_ArtistGetsCreatedWhenItDoesntExist(){
        when(artistRepo.existsBySpotifyId("test")).thenReturn(false);
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
        when(artistRepo.existsBySpotifyId("test")).thenReturn(false);
        when(artistRepo.save(any(ArtistEntity.class))).thenAnswer(InvocationOnMock -> InvocationOnMock.getArgument(0));

        ArtistCreatedResponse response = catalogService.createArtist(new ArtistCreatedRequest("Bladee", "test"));

        assertThat(response.name()).isEqualTo("Bladee");
        assertThat(response.spotifyId()).isEqualTo("test");

    }

    @Test
    void createArtist_CreateArtistPublishesEvent(){
        when(artistRepo.existsBySpotifyId("test")).thenReturn(false);
        when(artistRepo.save(any(ArtistEntity.class))).thenAnswer(InvocationOnMock -> InvocationOnMock.getArgument(0));

        catalogService.createArtist(new ArtistCreatedRequest("Bladee", "test"));

        ArgumentCaptor<ArtistCreatedEvent> captor = ArgumentCaptor.forClass(ArtistCreatedEvent.class);
        verify(events).publishEvent(captor.capture());
        assertThat(captor.getValue().artist().getName()).isEqualTo("Bladee");
        assertThat(captor.getValue().artist().getSpotifyId()).isEqualTo("test");

    }

    @Test
    void createArtist_EnsureEventIsNotPublishedWhenArtistExists(){
        when(artistRepo.existsBySpotifyId("test")).thenReturn(true);
        assertThrows(ArtistAlreadyExists.class, () -> catalogService.createArtist(new ArtistCreatedRequest("Bladee", "test")));

        verify(events, never()).publishEvent(any());
    }

    @Test
    void createTrack_EnsureTrackDoesNotGetCreatedIfArtistDoesNotExist(){
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test", 1L);
        when(artistRepo.findBySpotifyId("test")).thenReturn(Optional.empty());
        assertThrows(ArtistNotFoundException.class, () -> catalogService.createTrack(req));

        verify(trackRepo, never()).save(any());
        verify(trackRepo, never()).existsBySpotifyId(any());
    }

    @Test
    void createTrack_EnsureTrackGetsCreatedWhenArtistExistsAndTrackDoesNotExist(){
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test", 1L);
        ArtistEntity artist = new ArtistEntity("Bladee", "test");
        when(artistRepo.findBySpotifyId("test")).thenReturn(Optional.of(artist));
        when(trackRepo.save(any(TrackEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(trackRepo.existsBySpotifyId("test")).thenReturn(false);

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

        when(artistRepo.findBySpotifyId("test")).thenReturn(Optional.of(artist));
        when(trackRepo.existsBySpotifyId("test")).thenReturn(true);

        assertThrows(TrackNotFoundException.class, () -> catalogService.createTrack(req));

        verify(trackRepo, never()).save(any());
    }

    @Test
    void createTrack_EnsureTrackEventIsPublishedWhenTrackGetsCreated(){
        ArtistEntity artist = new ArtistEntity("Bladee", "test1");
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test1", 1L);
        when(artistRepo.findBySpotifyId("test1")).thenReturn(Optional.of(artist));
        when(trackRepo.existsBySpotifyId("test")).thenReturn(false);
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
        ArtistEntity artist = new ArtistEntity("Bladee", "test1");
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test1", 1L);
        when(artistRepo.findBySpotifyId(any())).thenReturn(Optional.empty());
        assertThrows(ArtistNotFoundException.class, () -> catalogService.createTrack(req));

        verify(events, never()).publishEvent(any());
    }

    @Test
    void createTrack_EnsureTrackEventIsNotPublishedWhenTrackExits(){
        ArtistEntity artist = new ArtistEntity("Bladee", "test1");
        TrackCreatedRequest req = new TrackCreatedRequest("unreal", "test", "test1", 1L);

        when(artistRepo.findBySpotifyId("test1")).thenReturn(Optional.of(artist));
        when(trackRepo.existsBySpotifyId("test")).thenReturn(true);

        assertThrows(TrackAlreadyExistsException.class, () -> catalogService.createTrack(req));

        verify(events, never()).publishEvent(any());
    }

    @Test
    void getArtistAndTrackByTrackId_verifyIfCorrectResponseIsReturned(){
        ArtistEntity artist = new ArtistEntity("Bladee", "test");
        TrackEntity track = new TrackEntity("spotifyId", artist, "unreal", 1L);
        when(trackRepo.findById(1L)).thenReturn(Optional.of(track));

        GetArtistAndTrackbyTrackIdDto res = catalogService.getArtistAndTrackByTrackId(1L);

        assertThat(res.artistName()).isEqualTo("Bladee");
        assertThat(res.trackName()).isEqualTo("unreal");
    }

    @Test
    void getArtistAndTrackByTrackId_throwsExceptionWhenTrackNotFound(){
        when(trackRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(TrackNotFoundException.class, () -> catalogService.getArtistAndTrackByTrackId(1L));
    }

    @Test
    void getArtistNameById_verifyThatNameIsReturnedIfArtistExists(){
        ArtistEntity artist = new ArtistEntity("Bladee", "spotifyId");
        when(artistRepo.findById(1L)).thenReturn(Optional.of(artist));

        String res = catalogService.getArtistNameById(1L);

        assertThat(res).isEqualTo("Bladee");
    }

    @Test
    void getArtistNameById_throwsExceptionWhenArtistIsNotFound(){
        when(artistRepo.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ArtistNotFoundException.class, () -> catalogService.getArtistNameById(1L));
    }
}
