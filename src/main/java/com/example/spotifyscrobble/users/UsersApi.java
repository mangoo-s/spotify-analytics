package com.example.spotifyscrobble.users;

import java.util.UUID;

public interface UsersApi {

    GetUserByIdResponse getUserByUsername(String username);

    String getUsernameByUserId(UUID userId);
}
