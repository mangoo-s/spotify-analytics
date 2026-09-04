package com.example.spotifyscrobble.users.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
public class UserEntity {

    @Id
    @NotNull
    private UUID userId;

    @Email
    @NotBlank(message = "Email cannot be blank")
    private String email;

    @Column(unique = true)
    @Setter
    private String username;

    @CreatedDate
    private Instant createdAt;

    protected UserEntity() {}

    public boolean isComplete(){
        return this.username != null;
    }

    public UserEntity(UUID userId, String email){
        this.userId = userId;
        this.createdAt = Instant.now();
        this.email = email;
    }
}
