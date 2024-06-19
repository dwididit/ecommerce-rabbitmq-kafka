package dev.dwidi.ecommercerabbitmqkafka.controller.auth;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserLoginRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserLoginResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.service.UserService;
import dev.dwidi.ecommercerabbitmqkafka.service.auth.UserAuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserAuthController {

    @Autowired
    private UserAuthService userAuthService;

    @Autowired
    private UserService userService;

    @Operation(summary = "Register a new user", description = "Create a new user account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    @PostMapping("/register")
    public PublicResponseDTO<UserResponseDTO> createUser(
            @Parameter(description = "User registration data") @RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }

    @Operation(summary = "Login user", description = "Authenticate user and generate access and refresh tokens")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    @PostMapping("/login")
    public PublicResponseDTO<UserLoginResponseDTO> loginUser(
            @Parameter(description = "User login data") @RequestBody UserLoginRequestDTO userLoginRequestDTO) {
        return userAuthService.loginUser(userLoginRequestDTO);
    }

    @Operation(summary = "Refresh token", description = "Generate a new access token using the refresh token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
            @ApiResponse(responseCode = "401", description = "Invalid refresh token")
    })
    @PostMapping("/refresh-token")
    public PublicResponseDTO<UserLoginResponseDTO> refreshToken(
            @Parameter(description = "Authorization header with refresh token") @RequestHeader("Authorization") String authorizationHeader) {
        return userAuthService.refreshToken(authorizationHeader);
    }
}
