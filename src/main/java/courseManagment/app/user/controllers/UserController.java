package courseManagment.app.user.controllers;

import courseManagment.app.token.RefreshTokenRequestDTO;
import courseManagment.app.user.dto.AuthResponseDTO;
import courseManagment.app.user.dto.LoginRequestDTO;
import courseManagment.app.user.dto.RegisterUserDTO;
import courseManagment.app.user.dto.UserResponseDTO;
import courseManagment.app.user.entity.User;
import courseManagment.app.user.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto){
        AuthResponseDTO message = userService.login(dto);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@Valid @RequestBody RefreshTokenRequestDTO dto){
        AuthResponseDTO response = userService.refreshToken(dto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequestDTO dto){
        userService.logout(dto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/tokens/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cleanupRefreshTokens() {
        int deleted = userService.cleanupExpiredOrRevokedTokens();
        return ResponseEntity.ok("Deleted refresh tokens: " + deleted);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@Valid @RequestBody RegisterUserDTO dto) {
        String message = userService.registerStudent(dto);
        return new ResponseEntity<>(message,HttpStatus.CREATED);
    }

    @GetMapping("/user/{name}")
    public ResponseEntity<UserResponseDTO> getUserByName(@PathVariable String name) {
        UserResponseDTO user = userService.getUserByName(name);
        return ResponseEntity.ok(user);
    }
}
