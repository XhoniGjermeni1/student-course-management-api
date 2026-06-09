package courseManagment.app.user.dto;

import courseManagment.app.user.entity.Role;

import java.util.UUID;

public class UserResponseDTO {
    private UUID id;
    private String username;
    private String email;
    private Role role;

    public UserResponseDTO() {
    }

    public UserResponseDTO(UUID id, String username, String email, Role role) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

}
