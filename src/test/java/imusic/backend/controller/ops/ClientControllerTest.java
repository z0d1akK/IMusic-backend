package imusic.backend.controller.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.create.ops.ClientCreateDto;
import imusic.backend.dto.request.ops.ClientRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.ClientResponseDto;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.dto.update.ops.ClientUpdateDto;
import imusic.backend.entity.ops.User;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.ops.ClientService;
import imusic.backend.token.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ClientController.class)
class ClientControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private JwtService jwtService;
    @MockBean private ClientService clientService;
    @MockBean private AuthService authService;
    @MockBean private UserMapper userMapper;
    @MockBean private RoleResolver roleResolver;
    @MockBean private UserStatusResolver userStatusResolver;

    @Test
    @DisplayName("GET /api/clients — возвращает всех клиентов")
    @WithMockUser
    void testGetAllClients() throws Exception {
        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(1L);

        when(clientService.getAllClients(any())).thenReturn(List.of(dto));

        mvc.perform(get("/api/clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("GET /api/clients/{id} — возвращает клиента по ID")
    @WithMockUser
    void testGetClientById() throws Exception {
        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(2L);

        when(clientService.getClientById(2L)).thenReturn(dto);

        mvc.perform(get("/api/clients/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @DisplayName("GET /api/clients/profile — возвращает профиль клиента")
    @WithMockUser(roles = "CLIENT")
    void testGetProfile() throws Exception {
        User user = new User();
        user.setId(5L);

        UserResponseDto userResponseDto = new UserResponseDto();
        userResponseDto.setId(5L);

        ClientResponseDto dto = new ClientResponseDto();
        dto.setId(5L);

        when(authService.getCurrentUser()).thenReturn(userResponseDto);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);
        when(clientService.getClientProfileByUserId(5L)).thenReturn(dto);

        mvc.perform(get("/api/clients/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    @DisplayName("POST /api/clients — создает клиента")
    @WithMockUser(roles = "ADMIN")
    void testCreateClient() throws Exception {
        ClientCreateDto createDto = new ClientCreateDto();
        createDto.setUserId(1L);

        ClientResponseDto response = new ClientResponseDto();
        response.setId(10L);

        when(clientService.createClient(any())).thenReturn(response);

        mvc.perform(post("/api/clients")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("PUT /api/clients/{id} — обновляет клиента")
    @WithMockUser(roles = "MANAGER")
    void testUpdateClient() throws Exception {
        ClientUpdateDto updateDto = new ClientUpdateDto();
        ClientResponseDto response = new ClientResponseDto();
        response.setId(20L);

        when(clientService.updateClient(eq(20L), any())).thenReturn(response);

        mvc.perform(put("/api/clients/20")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(20L));
    }

    @Test
    @DisplayName("DELETE /api/clients/{id} — удаляет клиента")
    @WithMockUser(roles = "ADMIN")
    void testDeleteClient() throws Exception {
        doNothing().when(clientService).deleteClient(30L);

        mvc.perform(delete("/api/clients/30")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(clientService).deleteClient(30L);
    }

    @Test
    @DisplayName("POST /api/clients/paged — пагинация клиентов")
    @WithMockUser
    void testGetPagedClients() throws Exception {
        ClientRequestDto request = new ClientRequestDto();
        request.setPage(0);
        request.setSize(10);

        PageResponseDto<ClientResponseDto> page = new PageResponseDto<>(List.of(),0,10,0,0);
        when(clientService.getPagedClients(any())).thenReturn(page);

        mvc.perform(post("/api/clients/paged")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
