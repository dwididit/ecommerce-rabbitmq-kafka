package dev.dwidi.ecommercerabbitmqkafka.controller;

import dev.dwidi.ecommercerabbitmqkafka.config.UUIDGeneratorConfig;
import dev.dwidi.ecommercerabbitmqkafka.dto.PublicResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserRequestDTO;
import dev.dwidi.ecommercerabbitmqkafka.dto.user.UserResponseDTO;
import dev.dwidi.ecommercerabbitmqkafka.exception.UserNotFoundException;
import dev.dwidi.ecommercerabbitmqkafka.security.SecurityConstants;
import dev.dwidi.ecommercerabbitmqkafka.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
@Import(UUIDGeneratorConfig.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private SecurityConstants securityConstants;
    
    private UserRequestDTO userRequestDTO;
    private UserResponseDTO userResponseDTO;

    @BeforeEach
    public void setUp() {
        userRequestDTO = new UserRequestDTO();
        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetAllUsers() throws Exception {
        when(userService.getAllUsers(any(), any())).thenReturn(new PublicResponseDTO<>("requestId", 200, "Success", Collections.singletonList(userResponseDTO)));

        mockMvc.perform(get("/api/user/all")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("Success")));

        verify(userService, times(1)).getAllUsers(any(), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetAllUsersAccessDenied() throws Exception {
        mockMvc.perform(get("/api/user/all"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testUpdateUser() throws Exception {
        when(userService.updateUser(anyLong(), any())).thenReturn(new PublicResponseDTO<>("requestId", 200, "Success", userResponseDTO));

        mockMvc.perform(put("/api/user/update")
                        .param("userId", "1")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"test\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("Success")));

        verify(userService, times(1)).updateUser(anyLong(), any());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testUpdateUserAccessDenied() throws Exception {
        mockMvc.perform(put("/api/user/update")
                        .param("userId", "1")
                        .contentType(APPLICATION_JSON)
                        .content("{\"username\":\"test\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testDeleteUser() throws Exception {
        when(userService.deleteUser(anyLong())).thenReturn(new PublicResponseDTO<>("requestId", 200, "Success", userResponseDTO));

        mockMvc.perform(delete("/api/user/delete")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("Success")));

        verify(userService, times(1)).deleteUser(anyLong());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testDeleteUserAccessDenied() throws Exception {
        mockMvc.perform(delete("/api/user/delete")
                        .param("userId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserById() throws Exception {
        when(userService.getUserById(anyLong())).thenReturn(new PublicResponseDTO<>("requestId", 200, "Success", userResponseDTO));

        mockMvc.perform(get("/api/user")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code", is(200)))
                .andExpect(jsonPath("$.message", is("Success")));

        verify(userService, times(1)).getUserById(anyLong());
    }

    @Test
    @WithMockUser(roles = "USER")
    public void testGetUserByIdAccessDenied() throws Exception {
        mockMvc.perform(get("/api/user")
                        .param("userId", "1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void testGetUserByIdNotFound() throws Exception {
        when(userService.getUserById(anyLong())).thenThrow(new UserNotFoundException("User not found"));

        mockMvc.perform(get("/api/user")
                        .param("userId", "999"))
                .andExpect(status().isNotFound());
    }
}
