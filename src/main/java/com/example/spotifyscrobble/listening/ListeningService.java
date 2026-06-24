package com.example.spotifyscrobble.listening;

import com.example.spotifyscrobble.shared.CustomPageResponse;
import com.example.spotifyscrobble.users.UserEntity;
import com.example.spotifyscrobble.users.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ListeningService {
    private final ApplicationEventPublisher events;
    private final ListeningHistoryRepository listeningHistoryRepo;
    private final UserRepository userRepo;

    public ListeningService(ApplicationEventPublisher events, ListeningHistoryRepository listeningHistoryRepo, UserRepository userRepo){
        this.events = events;
        this.listeningHistoryRepo = listeningHistoryRepo;
        this.userRepo = userRepo;
    }

    @Transactional
    public void processTrackListen(TrackListenedRequest trackListenedRequest) {
        UserEntity user = userRepo.findById(trackListenedRequest.userId()).orElseThrow(() -> new IllegalArgumentException("this user does not exist"));
        ListenEventEntity entity = new ListenEventEntity(user, trackListenedRequest.artistName(),trackListenedRequest.trackName(), trackListenedRequest.spotifyTrackId(), trackListenedRequest.spotifyArtistId(), trackListenedRequest.playedAt());
        listeningHistoryRepo.save(entity);

        TrackListenedEvent event = new TrackListenedEvent(
                trackListenedRequest.userId(),
                trackListenedRequest.spotifyArtistId(),
                trackListenedRequest.spotifyTrackId(),
                trackListenedRequest.playedAt()
        );
        log.info("User {}'s track event is being published.", trackListenedRequest.userId());
        events.publishEvent(event);
    }

    public CustomPageResponse<ListeningHistoryResponse> getUserListeningHistory(Long userId, Pageable p){
        Page<ListeningHistoryResponse> result = listeningHistoryRepo.findAllByUser_UserId(userId, p)
                .map(entity -> new ListeningHistoryResponse(
                        entity.getArtistName(),
                        entity.getTrackName(),
                        entity.getSpotifyArtistId(),
                        entity.getSpotifyTrackId(),
                        entity.getPlayedAt()
                        )
                );
        return new CustomPageResponse<>(result);
    }
}
