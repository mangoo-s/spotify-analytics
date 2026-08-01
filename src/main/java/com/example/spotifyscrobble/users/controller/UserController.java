package com.example.spotifyscrobble.users.controller;

import com.example.spotifyscrobble.users.components.CreateProfile;
import com.example.spotifyscrobble.users.dto.UserRegisterRequest;
import com.example.spotifyscrobble.users.dto.UsernameRequest;
import com.example.spotifyscrobble.users.entity.UserEntity;
import com.example.spotifyscrobble.users.repository.UserRepository;
import com.example.spotifyscrobble.users.service.UserService;
import jakarta.validation.Valid;
import org.jspecify.annotations.Nullable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

import java.util.Map;

@RestController
public class UserController {
    private final UserService userService;
    private final UserRepository userRepo;
    private final CreateProfile createProfile;
    private final RestClient restClient;

    public UserController(UserService userService, UserRepository userRepo, CreateProfile createProfile, RestClient restClient){
        this.userService = userService;
        this.userRepo = userRepo;
        this.createProfile = createProfile;
        this.restClient = restClient;
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

    @GetMapping("/test/spotify")
    public String testSpotify(@RegisteredOAuth2AuthorizedClient("spotifyscrobble")OAuth2AuthorizedClient authorizedClient){
        System.out.println(authorizedClient.getAccessToken());
        String hi = restClient.get()
                .uri("https://api.spotify.com/v1/me/player/currently-playing")
                .headers(h -> h.setBearerAuth(authorizedClient.getAccessToken().getTokenValue()))
                .retrieve()
                .body(String.class);
        System.out.println(hi);
        return hi;
    }
}
