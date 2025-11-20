package imusic.backend.controller.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.create.ops.InventoryMovementCreateDto;
import imusic.backend.dto.request.ops.InventoryMovementRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.InventoryMovementResponseDto;
import imusic.backend.dto.update.ops.InventoryMovementUpdateDto;
import imusic.backend.service.ops.InventoryMovementService;
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

@WebMvcTest(InventoryMovementController.class)
class InventoryMovementControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private InventoryMovementService movementService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("GET /api/inventory-movements — возвращает список")
    @WithMockUser
    void testGetAll() throws Exception {
        InventoryMovementResponseDto dto = new InventoryMovementResponseDto();
        dto.setId(1L);

        when(movementService.getAll()).thenReturn(List.of(dto));

        mvc.perform(get("/api/inventory-movements"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("GET /api/inventory-movements/{id} — возвращает объект")
    @WithMockUser
    void testGetById() throws Exception {
        InventoryMovementResponseDto dto = new InventoryMovementResponseDto();
        dto.setId(5L);

        when(movementService.getById(5L)).thenReturn(dto);

        mvc.perform(get("/api/inventory-movements/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    @DisplayName("POST /api/inventory-movements — создаёт движение (ADMIN/MANAGER)")
    @WithMockUser(roles = "ADMIN")
    void testCreate() throws Exception {
        InventoryMovementCreateDto createDto = new InventoryMovementCreateDto();
        createDto.setProductId(1L);
        createDto.setMovementTypeId(2L);
        createDto.setQuantity(10);
        createDto.setCreatedById(3L);

        InventoryMovementResponseDto dto = new InventoryMovementResponseDto();
        dto.setId(10L);

        when(movementService.create(any())).thenReturn(dto);

        mvc.perform(post("/api/inventory-movements")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("PUT /api/inventory-movements/{id} — обновляет (ADMIN/MANAGER)")
    @WithMockUser(roles = "MANAGER")
    void testUpdate() throws Exception {
        InventoryMovementUpdateDto updateDto = new InventoryMovementUpdateDto();
        updateDto.setQuantity(20);
        updateDto.setMovementTypeId(2L);

        InventoryMovementResponseDto dto = new InventoryMovementResponseDto();
        dto.setId(7L);

        when(movementService.update(eq(7L), any())).thenReturn(dto);

        mvc.perform(put("/api/inventory-movements/7")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(7L));
    }

    @Test
    @DisplayName("DELETE /api/inventory-movements/{id} — удаляет (ADMIN/MANAGER)")
    @WithMockUser(roles = "ADMIN")
    void testDelete() throws Exception {
        doNothing().when(movementService).delete(9L);

        mvc.perform(delete("/api/inventory-movements/9")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(movementService, times(1)).delete(9L);
    }

    @Test
    @DisplayName("POST /api/inventory-movements/paged — пагинация")
    @WithMockUser
    void testPaged() throws Exception {
        InventoryMovementRequestDto req = new InventoryMovementRequestDto();
        PageResponseDto<InventoryMovementResponseDto> page =
                new PageResponseDto<>(List.of(), 0, 10, 0, 0);

        when(movementService.getPagedMovements(any())).thenReturn(page);

        mvc.perform(post("/api/inventory-movements/paged")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
