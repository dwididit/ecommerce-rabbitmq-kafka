package dev.dwidi.ecommercerabbitmqkafka.controller.auth;

import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserLoginRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserLoginResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.service.UserService;
import dev.dwidi.ecommercerabbitmqkafka.service.auth.UserAuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserAuthControllerTest {

    @InjectMocks
    private UserAuthController userAuthController;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private UserService userService;

    @Mock
    private Supplier<String> uuidSupplier;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(userAuthController)
                .build();
    }

    @Test
    void loginUser_invalidCredentials() throws Exception {
        String fixedUUID = "32935e66-6118-4a61-b862-86685485f2b7";
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
        userLoginRequestDTO.setUsername("username");
        userLoginRequestDTO.setPassword("wrongpassword");

        PublicResponseDTO<UserLoginResponseDTO> responseDTO = new PublicResponseDTO<>(fixedUUID, 401, "Invalid username or password", null);

        when(uuidSupplier.get()).thenReturn(fixedUUID);
        when(userAuthService.loginUser(any(UserLoginRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\": \"test@example.com\", \"password\": \"wrongpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(fixedUUID))
                .andExpect(jsonPath("$.code").value(401))
                .andExpect(jsonPath("$.message").value("Invalid username or password"))
                .andExpect(jsonPath("$.data").isEmpty());
    }
    @Test
    void loginUser_success() throws Exception {
        String fixedUUID = "ed0f2a92-d154-4cac-9cf1-e2e4a5e42825";
        UserLoginRequestDTO userLoginRequestDTO = new UserLoginRequestDTO();
        userLoginRequestDTO.setUsername("username");
        userLoginRequestDTO.setPassword("correctpassword");

        UserLoginResponseDTO userLoginResponseDTO = new UserLoginResponseDTO();
        userLoginResponseDTO.setAccessToken("access_token");
        userLoginResponseDTO.setRefreshToken("refresh_token");

        PublicResponseDTO<UserLoginResponseDTO> responseDTO = new PublicResponseDTO<>(fixedUUID, 200, "User authenticated successfully", userLoginResponseDTO);

        when(uuidSupplier.get()).thenReturn(fixedUUID);
        when(userAuthService.loginUser(any(UserLoginRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content("{\"email\": \"test@example.com\", \"password\": \"correctpassword\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.requestId").value(fixedUUID))
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("User authenticated successfully"))
                .andExpect(jsonPath("$.data.accessToken").value("access_token"))
                .andExpect(jsonPath("$.data.refreshToken").value("refresh_token"));
    }
}
