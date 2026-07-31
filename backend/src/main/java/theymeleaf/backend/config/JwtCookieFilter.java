package theymeleaf.backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import theymeleaf.backend.entities.User;
import theymeleaf.backend.repositories.UserRepository;
import theymeleaf.backend.services.JwtService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserRepository userRepo;

    public JwtCookieFilter(JwtService jwtService, UserRepository userRepo) {
        this.jwtService = jwtService;
        this.userRepo = userRepo;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        readCookie(request)
                .flatMap(jwtService::verify)
                .flatMap(this::loadValidUser)
                .ifPresent(this::authenticate);

        chain.doFilter(request, response);
    }

    private Optional<String> readCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) return Optional.empty();

        for (Cookie cookie : cookies) {
            if ("session".equals(cookie.getName())) {
                return Optional.of(cookie.getValue());
            }
        }
        return Optional.empty();
    }

    private Optional<UserWithToken> loadValidUser(JwtService.TokenData token) {
        Optional<User> found = userRepo.findById(token.userId());

        if (found.isEmpty()) return Optional.empty();

        User user = found.get();

        if (!user.isActive()) return Optional.empty();

        // Token issued before the last invalidation (password set / deactivation)
        // is dead — the revocation trick.
        if (user.getSessionsInvalidatedAt() != null
                && token.issuedAt().isBefore(user.getSessionsInvalidatedAt())) {
            return Optional.empty();
        }

        return Optional.of(new UserWithToken(user));
    }

    private void authenticate(UserWithToken result) {
        User user = result.user();

        var authority = new SimpleGrantedAuthority("ROLE_" + user.getRole().name());
        var auth = new UsernamePasswordAuthenticationToken(user, null, List.of(authority));

        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private record UserWithToken(User user) {}
}
