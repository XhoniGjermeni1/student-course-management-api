package courseManagment.app.user.controllers;

import courseManagment.app.token.RefreshToken;
import courseManagment.app.token.RefreshTokenRequestDTO;
import courseManagment.app.user.dto.AuthResponseDTO;
import courseManagment.app.user.dto.LoginRequestDTO;
import courseManagment.app.user.dto.RegisterUserDTO;
import courseManagment.app.user.entity.User;
import courseManagment.app.user.services.UserService;
import org.apache.coyote.Response;
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
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO dto){
        try{
            AuthResponseDTO message = userService.login(dto);
            return ResponseEntity.ok(message);
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO dto){
        try{
            AuthResponseDTO response = userService.refreshToken(dto);
            return ResponseEntity.ok(response);
        }catch(Exception e ){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody RefreshTokenRequestDTO dto){
        try{
            userService.logout(dto);
            return ResponseEntity.noContent().build();
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @DeleteMapping("/tokens/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> cleanupRefreshTokens() {
        int deleted = userService.cleanupExpiredOrRevokedTokens();
        return ResponseEntity.ok("Deleted refresh tokens: " + deleted);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@RequestBody RegisterUserDTO dto) {
        try {
            String message = userService.registerStudent(dto);
            return new ResponseEntity<>(message,HttpStatus.CREATED);
        } catch (Exception e) {
        return new ResponseEntity<>("Gabim " + e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
//
//    @PostMapping("/register")
//    public ResponseEntity<String> registerUser(@RequestBody RegisterUserDTO dto) {
//       try{ String messge = userService.registerUser(dto);
//           return new ResponseEntity<>(messge,HttpStatus.CREATED);
//       }catch (Exception e) {
//           return new ResponseEntity<>("Gabim " + e.getMessage(), HttpStatus.BAD_REQUEST);
//        }
//    }

    @GetMapping("/user/{name}")
    public ResponseEntity<User> getUserByName(@PathVariable String name) {
        User user = userService.getUserByName(name);
        return ResponseEntity.ok(user);
    }
}
