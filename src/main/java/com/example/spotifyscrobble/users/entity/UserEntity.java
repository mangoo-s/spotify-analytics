package com.example.spotifyscrobble.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
public class UserEntity {

    @Id
    private UUID userId;

    @Email
    private String email;

    @NotBlank(message = "username cannot be blank")
    @Column(unique = true)
    @Setter
    private String username;

    @CreatedDate
    private Instant createdAt;



    protected UserEntity() {}

    public UserEntity(UUID userId, String username, String email){
        this.userId = userId;
        this.username = username;
        this.createdAt = Instant.now();
        this.email = email;
    }
}
