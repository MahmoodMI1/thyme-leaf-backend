package theymeleaf.backend.dtos;

import theymeleaf.backend.entities.User;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public record UserDto(Long id, String username, String role, String status, String created) {

    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getRole().name().toLowerCase(),
                user.isActive() ? "active" : "deactivated",
                user.getCreatedAt() == null ? null
                        : DATE.format(user.getCreatedAt().atZone(ZoneOffset.UTC))
        );
    }
}
