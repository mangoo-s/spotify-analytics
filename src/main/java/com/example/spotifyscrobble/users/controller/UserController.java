package com.example.spotifyscrobble.users.controller;

import com.example.spotifyscrobble.users.components.CreateProfile;
import com.example.spotifyscrobble.users.components.SpotifyApiClient;
import com.example.spotifyscrobble.users.dto.*;
import com.example.spotifyscrobble.users.entity.SpotifyConnectionEntity;
import com.example.spotifyscrobble.users.entity.UserEntity;
import com.example.spotifyscrobble.users.other.CurrentlyPlayingResult;
import com.example.spotifyscrobble.users.repository.SpotifyConnectionRepository;
import com.example.spotifyscrobble.users.repository.UserRepository;
import com.example.spotifyscrobble.users.service.SpotifyService;
import com.example.spotifyscrobble.users.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepo;
    private final CreateProfile createProfile;
    private final JwtDecoder jwtDecoder;
    private final SpotifyService spotifyService;
    private final SpotifyConnectionRepository spotifyConnectionRepo;
    private final SpotifyApiClient apiClient;

    @Value("${SPOTIFY_CLIENT_ID}")
    private String clientId;

    @Value("${SPOTIFY_CLIENT_SECRET}")
    private String clientSecret;

    public UserController(UserService userService, UserRepository userRepo, CreateProfile createProfile, JwtDecoder jwtDecoder, SpotifyService spotifyService, SpotifyConnectionRepository spotifyConnectionRepo, SpotifyApiClient apiClient){
        this.userService = userService;
        this.userRepo = userRepo;
        this.createProfile = createProfile;
        this.jwtDecoder = jwtDecoder;
        this.spotifyService = spotifyService;
        this.spotifyConnectionRepo = spotifyConnectionRepo;
        this.apiClient = apiClient;
    }

    @PutMapping("/profile/username")
    public ResponseEntity<?> createUsername(@AuthenticationPrincipal Jwt jwt, @RequestBody @Valid UsernameRequest usernameRequest){
        UserEntity user = createProfile.getOrCreateProfile(jwt);

        if(userRepo.existsByUsername(usernameRequest.username())){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already taken");
        }

        user.setUsername(usernameRequest.username());
        userRepo.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("Username successfully created.");
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> me(@AuthenticationPrincipal Jwt jwt){
        Map<String, Object> test = Map.of(
                "userId", jwt.getSubject(),
                "email", jwt.getClaimAsString("email")
        );

        return ResponseEntity.status(HttpStatus.OK).body(test);
    }

    @GetMapping("/spotify/connect")
    public void connectSpotify(@RequestParam("token") String token, HttpServletResponse response) throws IOException {
        Jwt jwt = jwtDecoder.decode(token);
        String state = jwt.getSubject();

        String authorizeUri = UriComponentsBuilder
                    .fromUriString("https://accounts.spotify.com/authorize")
                    .queryParam("client_id", clientId)
                    .queryParam("scope", "user-read-currently-playing,user-read-recently-played")
                    .queryParam("redirect_uri", "http://127.0.0.1:8080/callback")
                    .queryParam("state", state)
                    .queryParam("response_type", "code")
                    .build()
                    .toUriString();

        response.sendRedirect(authorizeUri);
    }

    @GetMapping("/callback")
    public ResponseEntity<?> spotifyCallback(@RequestParam("code") String code, @RequestParam("state") String state){
        apiClient.spotifyCallback(code, state);
        return ResponseEntity.status(HttpStatus.OK).body("Spotify connected");
    }

    @GetMapping("/current")
    public CurrentlyPlayingResult playingTrack(@AuthenticationPrincipal Jwt jwt){
        SpotifyConnectionEntity user = spotifyConnectionRepo.findById(UUID.fromString(jwt.getSubject())).orElseThrow(() -> new RuntimeException("yo"));
        return apiClient.getRecentlyPlayedTracks(user, user.getAfter());

    }
}
