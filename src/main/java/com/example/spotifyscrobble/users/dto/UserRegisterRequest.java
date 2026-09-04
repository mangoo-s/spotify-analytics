package com.example.spotifyscrobble.users.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRegisterRequest {
    @NotBlank(message = "Username cannot be null")
    private String username;

}
