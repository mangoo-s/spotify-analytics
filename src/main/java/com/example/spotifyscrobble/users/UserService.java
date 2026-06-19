package com.example.spotifyscrobble.users;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import com.example.spotifyscrobble.shared.UserAlreadyExistsException;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public void createUser(UserRegisterRequest userRegisterRequest){
        try{
            UserEntity entity = new UserEntity(userRegisterRequest.getUsername());
            userRepository.save(entity);
        }catch(DataIntegrityViolationException ex){
            throw new UserAlreadyExistsException("This user already exists.");
        }
    }
}
