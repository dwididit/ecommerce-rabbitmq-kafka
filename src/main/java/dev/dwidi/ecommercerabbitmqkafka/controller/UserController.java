package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Update a user", description = "Update an existing user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("@customSecurityExpression.isAdminOrSelf(#userId)")
    @PutMapping("/update")
    public PublicResponseDTO<UserResponseDTO> updateUser(
            @Parameter(description = "ID of the user to be updated") @RequestParam Long userId,
            @RequestBody UserRequestDTO userRequestDTO) {
        return userService.updateUser(userId, userRequestDTO);
    }

    @Operation(summary = "Delete a user", description = "Delete an existing user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("@customSecurityExpression.isAdminOrSelf(#userId)")
    @DeleteMapping("/delete")
    public PublicResponseDTO<UserResponseDTO> deleteUser(
            @Parameter(description = "ID of the user to be deleted") @RequestParam Long userId) {
        return userService.deleteUser(userId);
    }

    @Operation(summary = "Get all users", description = "Retrieve all users with optional pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/all")
    public PublicResponseDTO<List<UserResponseDTO>> getAllUsers(
            @Parameter(description = "Page number for pagination") @RequestParam(value = "page", required = false) Integer page,
            @Parameter(description = "Page size for pagination") @RequestParam(value = "size", required = false) Integer size) {
        return userService.getAllUsers(page, size);
    }

    @Operation(summary = "Get a user by ID", description = "Retrieve a specific user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "User not found"),
            @ApiResponse(responseCode = "403", description = "Access forbidden")
    })
    @PreAuthorize("@customSecurityExpression.isAdminOrSelf(#userId)")
    @GetMapping
    public PublicResponseDTO<UserResponseDTO> getUserById(
            @Parameter(description = "ID of the user to be retrieved") @RequestParam Long userId) {
        return userService.getUserById(userId);
    }
}
