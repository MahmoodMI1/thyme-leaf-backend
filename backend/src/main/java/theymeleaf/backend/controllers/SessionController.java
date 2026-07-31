package theymeleaf.backend.controllers;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import theymeleaf.backend.entities.User;
import theymeleaf.backend.services.AuthService;
import theymeleaf.backend.services.JwtService;
import theymeleaf.backend.dtos.LoginRequest;
import theymeleaf.backend.dtos.SessionDto;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/session")
public class SessionController {

    private final AuthService authService;
    private final JwtService jwtService;

    public SessionController(AuthService authService, JwtService jwtService) {
        this.authService = authService;
        this.jwtService = jwtService;
    }

    @PostMapping
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<User> result;

        try {
            result = authService.login(request.username(), request.password());
        } catch (AuthService.DeactivatedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "This account has been deactivated"));
        }

        if (result.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "That username and password don't match"));
        }

        User user = result.get();
        String token = jwtService.issue(user.getId());

        ResponseCookie cookie = ResponseCookie.from("session", token)
                .httpOnly(true)
                .sameSite("Strict")
                .secure(false)          // TODO prod profile: true
                .path("/")
                .maxAge(Duration.ofHours(8))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(new SessionDto(user.getUsername(), user.getRole().name().toLowerCase()));
    }
}