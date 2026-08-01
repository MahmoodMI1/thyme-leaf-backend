package theymeleaf.backend.dtos;

import jakarta.validation.constraints.NotBlank;

public record PasswordRequest(
        @NotBlank(message = "Password is required")
        String password
) {}
