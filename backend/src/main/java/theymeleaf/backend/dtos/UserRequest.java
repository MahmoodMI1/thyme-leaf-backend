package theymeleaf.backend.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import theymeleaf.backend.enums.Role;

public record UserRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 24, message = "Username must be 3-24 characters")
        @Pattern(regexp = "[a-zA-Z0-9._-]+", message = "Only letters, numbers, dots, dashes, underscores")
        String username,

        @NotNull(message = "Role is required")
        Role role,

        @NotBlank(message = "Password is required")
        String password
) {}