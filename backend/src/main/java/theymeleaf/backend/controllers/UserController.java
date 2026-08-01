package theymeleaf.backend.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import theymeleaf.backend.enums.Role;
import theymeleaf.backend.services.UserService;
import theymeleaf.backend.dtos.PasswordRequest;
import theymeleaf.backend.dtos.UserDto;
import theymeleaf.backend.dtos.UserRequest;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<UserDto> list() {
        return userService.list();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody UserRequest request) {
        return userService.create(request);
    }

    @PatchMapping("/{id}")
    public UserDto patch(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Role role = body.containsKey("role") ? Role.valueOf(body.get("role").toUpperCase()) : null;
        return userService.patch(id, role, body.get("status"));
    }

    @PostMapping("/{id}/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setPassword(@PathVariable Long id, @Valid @RequestBody PasswordRequest request) {
        userService.setPassword(id, request);
    }
}