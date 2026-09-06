package com.example.spotifyscrobble.users.service;

import com.example.spotifyscrobble.users.UsersApi;
import com.example.spotifyscrobble.users.components.CreateProfile;
import com.example.spotifyscrobble.users.GetUserByIdResponse;
import com.example.spotifyscrobble.users.repository.UserRepository;
import com.example.spotifyscrobble.users.entity.UserEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService implements UsersApi{
    private final UserRepository userRepo;
    private final CreateProfile createProfile;

    public UserService(UserRepository userRepo, CreateProfile createProfile){
        this.userRepo = userRepo;
        this.createProfile = createProfile;
    }

    public UserEntity createUserProfile(Jwt jwt){
        return createProfile.getOrCreateProfile(jwt);
    }


    @Override
    public GetUserByIdResponse getUserByUsername(String username) {
        UserEntity entity = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("hi")); //Need to create its own exception
        return new GetUserByIdResponse(entity.getUserId(), entity.getCreatedAt());
    }

    @Override
    @Cacheable(cacheNames = "usernames", key = "#userId")
    public String getUsernameByUserId(UUID userId){
        UserEntity entity = userRepo.findById(userId).orElseThrow(() -> new RuntimeException("hi")); // Need to create its own exception
        return entity.getUsername();
    }

}
