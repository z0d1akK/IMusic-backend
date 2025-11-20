package imusic.backend.controller.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.auth.ChangeLoginRequest;
import imusic.backend.dto.auth.ChangePasswordRequest;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.dto.update.ops.UserUpdateDto;
import imusic.backend.service.ops.UserService;
import imusic.backend.token.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void testGetAllUsers() throws Exception {
        UserResponseDto dto = UserResponseDto.builder()
                .id(1L)
                .username("user1")
                .build();

        when(userService.getAllUsers()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("user1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testUpdateUser() throws Exception {
        UserUpdateDto dto = new UserUpdateDto();
        UserResponseDto responseDto = UserResponseDto.builder().id(1L).username("user1").build();

        when(userService.updateUser(1L, dto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("user1"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/api/users/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testChangePassword() throws Exception {
        ChangePasswordRequest req = new ChangePasswordRequest();
        doNothing().when(userService).changePassword(1L, req);

        mockMvc.perform(put("/api/users/{id}/change-password", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(userService, times(1)).changePassword(1L, req);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testChangeLogin() throws Exception {
        ChangeLoginRequest req = new ChangeLoginRequest();
        doNothing().when(userService).changeLogin(1L, req);

        mockMvc.perform(put("/api/users/{id}/change-login", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk());

        verify(userService, times(1)).changeLogin(1L, req);
    }
}
