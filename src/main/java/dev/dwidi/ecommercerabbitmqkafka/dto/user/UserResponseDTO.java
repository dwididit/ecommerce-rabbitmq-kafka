package dev.dwidi.ecommercerabbitmqkafka.dto.user;

import dev.dwidi.ecommercerabbitmqkafka.entity.User;
import dev.dwidi.ecommercerabbitmqkafka.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String address;
    private String phone;
    private UserRole userRole;
    private LocalDateTime updatedAt;
    private LocalDateTime createdAt;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.address = user.getAddress();
        this.phone = user.getPhone();
        this.userRole = user.getUserRole();
        this.updatedAt = user.getUpdatedAt();
        this.createdAt = user.getCreatedAt();
    }
}
