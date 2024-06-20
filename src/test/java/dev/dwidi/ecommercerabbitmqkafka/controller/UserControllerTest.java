package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.exception.GlobalExceptionHandler;
import dev.dwidi.ecommercerabbitmqkafka.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private UserService userService;

    @Mock
    private Supplier<String> uuidSupplier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUser_success() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("updateduser");
        userRequestDTO.setPassword("newpassword");
        userRequestDTO.setEmail("updateduser@example.com");
        userRequestDTO.setAddress("456 Street");
        userRequestDTO.setPhone("0987654321");

        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
        userResponseDTO.setUsername("updateduser");
        userResponseDTO.setEmail("updateduser@example.com");
        userResponseDTO.setAddress("456 Street");
        userResponseDTO.setPhone("0987654321");

        PublicResponseDTO<UserResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 200, "User updated successfully", userResponseDTO);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(userService.updateUser(anyLong(), any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/user/update")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"updateduser\", \"password\": \"newpassword\", \"email\": \"updateduser@example.com\", \"address\": \"456 Street\", \"phone\": \"0987654321\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User updated successfully"))
                .andExpect(jsonPath("$.data.username").value("updateduser"))
                .andExpect(jsonPath("$.data.email").value("updateduser@example.com"))
                .andExpect(jsonPath("$.data.address").value("456 Street"))
                .andExpect(jsonPath("$.data.phone").value("0987654321"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void updateUser_userNotFound() throws Exception {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setUsername("updateduser");
        userRequestDTO.setPassword("newpassword");
        userRequestDTO.setEmail("updateduser@example.com");
        userRequestDTO.setAddress("456 Street");
        userRequestDTO.setPhone("0987654321");

        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<UserResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "User not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(userService.updateUser(anyLong(), any(UserRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/user/update")
                        .param("userId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"updateduser\", \"password\": \"newpassword\", \"email\": \"updateduser@example.com\", \"address\": \"456 Street\", \"phone\": \"0987654321\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_success() throws Exception {
        PublicResponseDTO<UserResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 200, "User deleted successfully", null);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(userService.deleteUser(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/user/delete")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deleteUser_userNotFound() throws Exception {
        String requestId = "a311a58e-59c5-4485-a2f3-ff1a6aee74cd";
        PublicResponseDTO<UserResponseDTO> responseDTO = new PublicResponseDTO<>(requestId, 404, "User not found", null);

        when(uuidSupplier.get()).thenReturn(requestId);
        when(userService.deleteUser(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(delete("/api/user/delete")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(requestId))
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("User not found"))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getAllUsers_success() throws Exception {
        List<UserResponseDTO> userResponseDTOList = List.of(
                new UserResponseDTO(1L, "user1", "user1@example.com", "Address1", "1234567890", null, null, null),
                new UserResponseDTO(2L, "user2", "user2@example.com", "Address2", "0987654321", null, null, null)
        );
        PublicResponseDTO<List<UserResponseDTO>> responseDTO = new PublicResponseDTO<>("requestId", 200, "Users retrieved successfully", userResponseDTOList);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(userService.getAllUsers(null, null)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("Users retrieved successfully"))
                .andExpect(jsonPath("$.data[0].username").value("user1"))
                .andExpect(jsonPath("$.data[1].username").value("user2"));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void getUserById_success() throws Exception {
        UserResponseDTO userResponseDTO = new UserResponseDTO(1L, "user1", "user1@example.com", "Address1", "1234567890", null, null, null);
        PublicResponseDTO<UserResponseDTO> responseDTO = new PublicResponseDTO<>("requestId", 200, "User retrieved successfully", userResponseDTO);

        when(uuidSupplier.get()).thenReturn("requestId");
        when(userService.getUserById(anyLong())).thenReturn(responseDTO);

        mockMvc.perform(get("/api/user")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User retrieved successfully"))
                .andExpect(jsonPath("$.data.username").value("user1"));
    }
}
