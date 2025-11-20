package imusic.backend.controller.ref;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.create.ref.ProductUnitCreateDto;
import imusic.backend.dto.request.ref.ProductUnitRequestDto;
import imusic.backend.dto.response.ref.ProductUnitResponseDto;
import imusic.backend.dto.update.ref.ProductUnitUpdateDto;
import imusic.backend.service.ref.ProductUnitService;
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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductUnitController.class)
class ProductUnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService tokenService;

    @MockBean
    private ProductUnitService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/ref/product-units - success for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testGetAll() throws Exception {
        ProductUnitResponseDto dto = new ProductUnitResponseDto(1L, "TEST_CODE", "Test Name");
        when(service.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ref/product-units")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("TEST_CODE"));

        verify(service, times(1)).getAll();
    }

    @Test
    @DisplayName("GET /api/ref/product-units/{id} - success for MANAGER")
    @WithMockUser(roles = "MANAGER")
    void testGetById() throws Exception {
        ProductUnitResponseDto dto = new ProductUnitResponseDto(1L, "TEST_CODE", "Test Name");
        when(service.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/ref/product-units/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST_CODE"));

        verify(service, times(1)).getById(1L);
    }

    @Test
    @DisplayName("POST /api/ref/product-units - create for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testCreate() throws Exception {
        ProductUnitCreateDto createDto = new ProductUnitCreateDto();
        createDto.setCode("TEST_CODE");
        createDto.setName("Test Name");

        ProductUnitResponseDto responseDto = new ProductUnitResponseDto(1L, "TEST_CODE", "Test Name");
        when(service.create(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/ref/product-units")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Name"));

        verify(service, times(1)).create(createDto);
    }

    @Test
    @DisplayName("PUT /api/ref/product-units/{id} - update for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testUpdate() throws Exception {
        ProductUnitUpdateDto updateDto = new ProductUnitUpdateDto();
        updateDto.setCode("NEW_CODE");
        updateDto.setName("New Name");

        ProductUnitResponseDto responseDto = new ProductUnitResponseDto(1L, "NEW_CODE", "New Name");
        when(service.update(1L, updateDto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/ref/product-units/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("NEW_CODE"))
                .andExpect(jsonPath("$.name").value("New Name"));

        verify(service, times(1)).update(1L, updateDto);
    }

    @Test
    @DisplayName("DELETE /api/ref/product-units/{id} - delete for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/ref/product-units/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1L);
    }

    @Test
    @DisplayName("POST /api/ref/product-units/filter - success for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testGetWithFilters() throws Exception {
        ProductUnitRequestDto request = new ProductUnitRequestDto();
        request.setPage(0);
        request.setSize(10);
        request.setCode("TEST");
        request.setName("Test");

        ProductUnitResponseDto dto = new ProductUnitResponseDto(1L, "TEST_CODE", "Test Name");
        when(service.getUnitsWithFilters(request)).thenReturn(List.of(dto));

        mockMvc.perform(post("/api/ref/product-units/filter")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("TEST_CODE"));

        verify(service, times(1)).getUnitsWithFilters(request);
    }
}
