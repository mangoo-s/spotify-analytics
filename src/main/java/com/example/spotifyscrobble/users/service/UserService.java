package com.example.spotifyscrobble.users.service;

import com.example.spotifyscrobble.users.UsersApi;
import com.example.spotifyscrobble.users.dto.UserRegisterRequest;
import com.example.spotifyscrobble.users.repository.UserRepository;
import com.example.spotifyscrobble.users.entity.UserEntity;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.spotifyscrobble.shared.UserAlreadyExistsException;

@Service
public class UserService implements UsersApi{
    private final UserRepository userRepo;

    public UserService(UserRepository userRepo){
        this.userRepo = userRepo;
    }

    public void createUser(UserRegisterRequest userRegisterRequest){
        try{
            UserEntity entity = new UserEntity(userRegisterRequest.getUsername());
            userRepo.save(entity);
        }catch(DataIntegrityViolationException ex){
            throw new UserAlreadyExistsException("This user already exists.");
        }
    }


}
