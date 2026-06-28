package com.example.spotifyscrobble.users.controller;

import com.example.spotifyscrobble.users.dto.UserRegisterRequest;
import com.example.spotifyscrobble.users.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody @Valid UserRegisterRequest userRegisterRequest){
        userService.createUser(userRegisterRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("New user created with username: "+userRegisterRequest.getUsername());
    }
}
