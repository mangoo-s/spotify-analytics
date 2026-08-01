package com.example.spotifyscrobble.users.service;

import com.example.spotifyscrobble.users.UsersApi;
import com.example.spotifyscrobble.users.components.CreateProfile;
import com.example.spotifyscrobble.users.dto.UserRegisterRequest;
import com.example.spotifyscrobble.users.repository.UserRepository;
import com.example.spotifyscrobble.users.entity.UserEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import com.example.spotifyscrobble.shared.UserAlreadyExistsException;

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



}
