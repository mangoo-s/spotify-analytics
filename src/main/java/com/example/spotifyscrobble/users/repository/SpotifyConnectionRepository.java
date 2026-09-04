package com.example.spotifyscrobble.users.repository;

import com.example.spotifyscrobble.users.entity.SpotifyConnectionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpotifyConnectionRepository extends JpaRepository<SpotifyConnectionEntity, UUID> {
}
