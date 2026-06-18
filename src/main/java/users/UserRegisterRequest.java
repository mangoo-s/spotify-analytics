package users;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class UserRegisterRequest {
    @NotBlank(message = "Username cannot be null")
    private String username;

}
