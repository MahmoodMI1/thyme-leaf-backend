package theymeleaf.backend.services;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import theymeleaf.backend.entities.User;
import theymeleaf.backend.repositories.UserRepository;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Empty = bad credentials (401). DeactivatedException = 403.
     * Success = the user.
     */
    @Transactional(readOnly = true)
    public Optional<User> login(String username, String password) {
        Optional<User> found = userRepo.findByUsernameIgnoreCase(username.trim());

        // Unknown user: still burn a bcrypt check so response timing
        // doesn't reveal whether the username exists.
        if (found.isEmpty()) {
            encoder.matches(password, "$2a$10$7EqJtq98hPqEX7fNZaFWoOhi5B0Wf1sO1sBqQ8mE9uS9uYq0eYm2u");
            return Optional.empty();
        }

        User user = found.get();

        if (!encoder.matches(password, user.getPasswordHash())) {
            return Optional.empty();
        }

        if (!user.isActive()) {
            throw new DeactivatedException();
        }

        return Optional.of(user);
    }

    public static class DeactivatedException extends RuntimeException {}
}