package imusic.backend.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.auth.LoginRequest;
import imusic.backend.dto.auth.RegisterRequest;
import imusic.backend.dto.auth.TokenResponse;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.service.auth.AuthService;
import imusic.backend.token.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @MockBean private JwtService jwtService;
    @MockBean private AuthService authService;

    @Test
    @DisplayName("POST /api/auth/login — успешный логин")
    void testLogin() throws Exception {
        LoginRequest request = new LoginRequest("user","Pass1234");
        TokenResponse token = new TokenResponse(1L,"token", List.of("CLIENT"));

        when(authService.login(any(LoginRequest.class))).thenReturn(token);

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.roles[0]").value("CLIENT"));
    }

    @Test
    @DisplayName("POST /api/auth/register — успешная регистрация")
    void testRegister() throws Exception {
        RegisterRequest request = new RegisterRequest("user","Pass1234","Pass1234","Full Name","user@mail.com");
        TokenResponse token = new TokenResponse(1L,"token", List.of("CLIENT"));

        when(authService.register(any(RegisterRequest.class))).thenReturn(token);

        mvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token"))
                .andExpect(jsonPath("$.roles[0]").value("CLIENT"));
    }

    @Test
    @DisplayName("GET /api/auth/me — получение текущего пользователя")
    @WithMockUser(username = "user")
    void testGetCurrentUser() throws Exception {
        UserResponseDto dto = new UserResponseDto();
        when(authService.getCurrentUser()).thenReturn(dto);

        mvc.perform(get("/api/auth/me"))
                .andExpect(status().isOk());
    }
}
