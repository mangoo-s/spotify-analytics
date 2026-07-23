package com.example.spotifyscrobble.users.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsernameRequest(
        @NotBlank(message = "Username can not be empty.")
        @Size(min = 3, max = 20, message = "Username must be between 3-20 characters.")
                              String username) {

}
