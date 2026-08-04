package com.example.spotifyscrobble.users.service;

import com.example.spotifyscrobble.users.dto.SpotifyAccessTokenResponse;
import com.example.spotifyscrobble.users.entity.SpotifyConnectionEntity;
import com.example.spotifyscrobble.users.repository.SpotifyConnectionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.Base64;
import java.util.UUID;

@Service
public class SpotifyService {

    @Value("${SPOTIFY_CLIENT_ID}")
    private String clientId;

    @Value("${SPOTIFY_CLIENT_SECRET}")
    private String clientSecret;

    private final SpotifyConnectionRepository spotifyConnectionRepo;

    public SpotifyService(SpotifyConnectionRepository spotifyConnectionRepo) {
        this.spotifyConnectionRepo = spotifyConnectionRepo;
    }

    public String checkValidSpotifyToken(UUID id){
        SpotifyConnectionEntity spotifyConnection = spotifyConnectionRepo.getReferenceById(id);
        return null;
    }

    public void spotifyCallback(String code, String state){
        RestClient rc = RestClient.create();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("code", code);
        body.add("redirect_uri", "http://127.0.0.1:8080/callback");

        String credentials = Base64.getEncoder().encodeToString((clientId + ":" + clientSecret).getBytes());

        SpotifyAccessTokenResponse tokenResponse = rc.post()
                .uri("https://accounts.spotify.com/api/token")
                .header("Authorization", "Basic "+credentials)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body(body)
                .retrieve()
                .body(SpotifyAccessTokenResponse.class);
        SpotifyConnectionEntity connectionEntity = new SpotifyConnectionEntity(UUID.fromString(state), tokenResponse.expiresIn(), tokenResponse.access_token(), tokenResponse.refresh_token());
        spotifyConnectionRepo.save(connectionEntity);
    }
}
