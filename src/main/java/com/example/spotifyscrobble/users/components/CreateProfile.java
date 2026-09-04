package com.example.spotifyscrobble.users.components;

import com.example.spotifyscrobble.users.dto.UserProfileResponse;
import com.example.spotifyscrobble.users.entity.UserEntity;
import com.example.spotifyscrobble.users.repository.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CreateProfile {
    private final UserRepository userRepo;

    public CreateProfile(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public UserEntity getOrCreateProfile(Jwt jwt){
        UUID userId = UUID.fromString(jwt.getSubject());
        return userRepo.findById(userId).orElseGet(
                () -> userRepo.save(new UserEntity(userId, jwt.getClaimAsString("email")))
        );
    }
}
