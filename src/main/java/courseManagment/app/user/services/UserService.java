package courseManagment.app.user.services;

import courseManagment.app.exception.NotFoundException;
import courseManagment.app.security.JwtService;
import courseManagment.app.student.entity.Student;
import courseManagment.app.token.RefreshToken;
import courseManagment.app.token.RefreshTokenRequestDTO;
import courseManagment.app.token.RefreshTokenService;
import courseManagment.app.user.dto.AuthResponseDTO;
import courseManagment.app.user.dto.LoginRequestDTO;
import courseManagment.app.user.dto.RegisterUserDTO;
import courseManagment.app.user.dto.UserResponseDTO;
import courseManagment.app.user.entity.Role;
import courseManagment.app.user.entity.User;
import courseManagment.app.user.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService,RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

//  Thirret authentication manager qe therret authenticationProvider dhe kontrollon
//  nese gjendet apo jo useri qe
//    eshte futur ne dto, ekziston apo jo ne db
//    Deri pa u krijuar jwt tokeni puna mbaron deri te autentikimi
//    Me tej nese eshte kaluar autentikimi krijojme nje access token
    public AuthResponseDTO login(LoginRequestDTO dto){
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getUsername(),
                        dto.getPassword()
                )
        );
        User user = userRepository.findByUsernameIgnoreCase(dto.getUsername())
                .orElseThrow(()-> new NotFoundException("User not found!"));

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        return new AuthResponseDTO(accessToken, refreshToken.getToken(), "Bearer");
    }

    public void logout(RefreshTokenRequestDTO dto){
        refreshTokenService.revokeRefreshToken(dto.getRefreshToken());
    }

    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO dto){
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(dto.getRefreshToken());
        User user = newRefreshToken.getUser();
        String accessToken = jwtService.generateAccessToken(user);
        return new AuthResponseDTO(accessToken, newRefreshToken.getToken(), "Bearer");
    }

    public int cleanupExpiredOrRevokedTokens() {
        return refreshTokenService.cleanupExpiredOrRevokedTokens();
    }


    @Transactional
    public String registerStudent(RegisterUserDTO dto){
        User user = new User();
        user.setUsername(dto.getUsername());
//        kemi passwordEncoder.encode per ta vendosur te hashuar kodin ne db
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());

        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
    if (user.getRole() == Role.STUDENT){
        Student student = new Student();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setActive(true);
        student.setUser(user);
        user.setStudent(student);

        userRepository.save(user);
        return "Student registered successfully: " + student;
        }
        userRepository.save(user);
    return "User registered successfully: " + user;
    }
//
//    @Transactional
//    public String registerUser(RegisterUserDTO dto){
//        User user = new User();
//        user.setUsername(dto.getUsername());
//        user.setPassword(dto.getPassword());
//        user.setEmail(dto.getEmail());
//
//        user.setRole(Role.valueOf(dto.getRole().toUpperCase()));
//        userRepository.save(user);
//        return "User registered successfully" + user.getRole();
//    }

    public UserResponseDTO getUserByName(String username){
       User user = userRepository.findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new NotFoundException("User nuk u gjet me emrin: " + username));
       return new UserResponseDTO(
               user.getId(),
               user.getUsername(),
               user.getEmail(),
               user.getRole()
       );
    }
}
