package com.example.spotifyscrobble.users.controller;

import com.example.spotifyscrobble.users.UsersApi;
import com.example.spotifyscrobble.users.components.SpotifyApiClient;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@RestController
public class SpotifyController {

    @Value("${SPOTIFY_CLIENT_ID}")
    private String clientId;

    @Value("${SPOTIFY_CLIENT_SECRET}")
    private String clientSecret;

    private final SpotifyApiClient spotifyApiClient;
    private final JwtDecoder jwtDecoder;


    public SpotifyController(SpotifyApiClient spotifyApiClient, JwtDecoder jwtDecoder) {
        this.spotifyApiClient = spotifyApiClient;
        this.jwtDecoder = jwtDecoder;
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
        System.out.println("Hello2");
        spotifyApiClient.spotifyCallback(code, state);
        return ResponseEntity.status(HttpStatus.OK).body("Spotify connected");
    }
}
