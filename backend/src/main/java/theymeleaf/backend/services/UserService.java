package theymeleaf.backend.services;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import theymeleaf.backend.enums.Role;
import theymeleaf.backend.entities.User;
import theymeleaf.backend.repositories.UserRepository;
import theymeleaf.backend.dtos.PasswordRequest;
import theymeleaf.backend.dtos.UserDto;
import theymeleaf.backend.dtos.UserRequest;

import java.time.Instant;
import java.util.List;

@Service
public class UserService {

    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder;
    private final PasswordPolicy passwordPolicy;

    public UserService(UserRepository repo, BCryptPasswordEncoder encoder, PasswordPolicy passwordPolicy) {
        this.repo = repo;
        this.encoder = encoder;
        this.passwordPolicy = passwordPolicy;
    }

    @Transactional(readOnly = true)
    public List<UserDto> list() {
        return repo.findAll().stream().map(UserDto::from).toList();
    }

    @Transactional
    public UserDto create(UserRequest request) {
        String username = request.username().toLowerCase().trim();

        if (repo.findByUsernameIgnoreCase(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A user with this username already exists");
        }

        passwordPolicy.check(request.password(), username);

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(encoder.encode(request.password()));
        user.setRole(request.role());
        return UserDto.from(repo.save(user));
    }

    /** Role and/or status change. Nulls mean "leave unchanged". */
    @Transactional
    public UserDto patch(Long id, Role role, String status) {
        User user = find(id);
        User current = currentUser();

        boolean self = current.getId().equals(id);

        if (role != null && role != user.getRole()) {
            if (self) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You cannot change your own role");
            }
            user.setRole(role);
        }

        if (status != null) {
            boolean activate = switch (status) {
                case "active" -> true;
                case "deactivated" -> false;
                default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Status must be 'active' or 'deactivated'");
            };

            if (!activate && self) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "You cannot deactivate your own account");
            }

            if (user.isActive() && !activate) {
                user.setSessionsInvalidatedAt(Instant.now());   // kill live sessions
            }
            user.setActive(activate);
        }

        return UserDto.from(repo.save(user));
    }

    @Transactional
    public void setPassword(Long id, PasswordRequest request) {
        User user = find(id);

        passwordPolicy.check(request.password(), user.getUsername());

        user.setPasswordHash(encoder.encode(request.password()));
        user.setSessionsInvalidatedAt(Instant.now());   // old sessions die
        repo.save(user);
    }

    // ---- helpers ----

    private User find(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private User currentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}