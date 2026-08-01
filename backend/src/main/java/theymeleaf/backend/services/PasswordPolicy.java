package theymeleaf.backend.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.Set;

@Component
public class PasswordPolicy {

    private static final Set<String> BLOCKLIST = Set.of(
            "password", "password1", "12345678", "123456789", "qwerty123",
            "letmein1", "iloveyou", "sunshine", "admin123", "welcome1",
            "holidaythyme", "holiday1", "thyme123", "kitchen1"
    );

    /** Throws 400 with a field-level style message if the password is unacceptable. */
    public void check(String password, String username) {
        if (password.length() < 8 || password.length() > 64) {
            reject("Password must be 8-64 characters");
        }
        String lower = password.toLowerCase(Locale.ROOT);
        if (username != null && lower.contains(username.toLowerCase(Locale.ROOT))) {
            reject("Password must not contain the username");
        }
        if (BLOCKLIST.contains(lower)) {
            reject("This password is too common");
        }
    }

    private void reject(String message) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
