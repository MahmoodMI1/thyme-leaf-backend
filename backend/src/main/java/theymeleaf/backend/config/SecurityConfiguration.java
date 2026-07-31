package theymeleaf.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfiguration {

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtCookieFilter jwtFilter) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(
                        org.springframework.security.config.http.SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/actuator/health").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/session").permitAll()
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/foods/**").hasAnyRole("ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.PATCH, "/api/foods/**").hasAnyRole("ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.DELETE, "/api/foods/**").hasAnyRole("ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.GET, "/api/foods").hasAnyRole("ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.POST, "/api/dishes/**").hasAnyRole("ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.PUT, "/api/dishes/**").hasAnyRole("ADMIN", "CHEF")
                        .requestMatchers(HttpMethod.DELETE, "/api/dishes/**").hasAnyRole("ADMIN", "CHEF")
                        .anyRequest().authenticated()
                )
                .exceptionHandling(e -> e.authenticationEntryPoint(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}