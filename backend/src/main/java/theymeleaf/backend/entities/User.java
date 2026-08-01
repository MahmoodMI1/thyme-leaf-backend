package theymeleaf.backend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import theymeleaf.backend.enums.Role;

import java.time.Instant;

@Entity
@Getter @Setter
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    @Column(name = "password_hash")
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private Role role;

    private boolean active = true;

    @Column(name = "sessions_invalidated_at")
    private Instant sessionsInvalidatedAt;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt = Instant.now();
}
