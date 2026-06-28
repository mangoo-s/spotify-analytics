package com.example.spotifyscrobble.users;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;

@Entity
@Getter
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @NotBlank(message = "username cannot be blank")
    @Column(unique = true)
    private String username;

    @CreatedDate
    private Instant createdAt;

    protected UserEntity() {}

    public UserEntity(String username){
        this.username = username;
    }
}
