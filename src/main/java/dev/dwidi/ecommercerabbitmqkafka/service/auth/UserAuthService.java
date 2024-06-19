package dev.dwidi.ecommercerabbitmqkafka.service.auth;

import com.auth0.jwt.interfaces.DecodedJWT;
import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserLoginRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserLoginResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.entity.User;
import dev.dwidi.ecommercerabbitmqkafka.exception.InvalidCredentialsException;
import dev.dwidi.ecommercerabbitmqkafka.repository.UserRepository;
import dev.dwidi.ecommercerabbitmqkafka.security.JWTUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;
import java.util.Optional;

@Service
public class UserAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JWTUtil jwtUtil;

    @Autowired
    private Supplier<String> uuidGenerator;

    public PublicResponseDTO<UserLoginResponseDTO> loginUser(UserLoginRequestDTO userLoginRequestDTO) {
        Optional<User> optionalUser = userRepository.findByUsername(userLoginRequestDTO.getUsername());
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User user = optionalUser.get();

        if (!passwordEncoder.matches(userLoginRequestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUsername(), user.getId(), user.getUserRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUsername());

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(accessToken, refreshToken);

        // Generate requestId
        String requestId = uuidGenerator.get();

        return new PublicResponseDTO<>(requestId, 200, "Login successful", userLoginResponseDTO);
    }

    public PublicResponseDTO<UserLoginResponseDTO> refreshToken(String authorizationHeader) {
        // Generate requestId
        String requestId = uuidGenerator.get();

        // Extract the token from the Bearer header
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return new PublicResponseDTO<>(requestId, 401, "invalid token", null);
        }

        String refreshToken = authorizationHeader.substring(7);

        DecodedJWT decodedJWT = jwtUtil.verifyToken(refreshToken);

        if (!decodedJWT.getClaim("type").asString().equals("REFRESH")) {
            throw new InvalidCredentialsException("Invalid refresh token");
        }

        String username = decodedJWT.getSubject();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid refresh token"));

        String newAccessToken = jwtUtil.generateAccessToken(username, user.getId(), user.getUserRole().name());
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO(newAccessToken, newRefreshToken);

        return new PublicResponseDTO<>(requestId, 200, "Token refreshed successfully", userLoginResponseDTO);
    }
}
