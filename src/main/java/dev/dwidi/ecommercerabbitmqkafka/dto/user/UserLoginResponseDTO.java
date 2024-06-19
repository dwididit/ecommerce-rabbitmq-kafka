package dev.dwidi.ecommercerabbitmqkafka.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginResponseDTO {
    private String accessToken;
    private String refreshToken;
}
