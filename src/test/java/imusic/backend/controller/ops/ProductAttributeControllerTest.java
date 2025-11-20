package imusic.backend.controller.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.create.ops.ProductAttributeCreateDto;
import imusic.backend.dto.request.ops.ProductAttributeRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;
import imusic.backend.dto.update.ops.ProductAttributeUpdateDto;
import imusic.backend.service.ops.ProductAttributeService;
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

@WebMvcTest(ProductAttributeController.class)
class ProductAttributeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private ProductAttributeService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/product-attributes — получить все атрибуты")
    @WithMockUser(roles = "ADMIN")
    void testGetAll() throws Exception {
        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("Red")
                .productId(10L)
                .build();

        when(service.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/product-attributes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value("Red"));
    }

    @Test
    @DisplayName("POST /api/product-attributes/paged — получить страницу атрибутов")
    @WithMockUser(roles = "ADMIN")
    void testGetPagedAttributes() throws Exception {
        ProductAttributeRequestDto req = ProductAttributeRequestDto.builder()
                .page(0)
                .size(10)
                .build();

        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("Red")
                .build();

        PageResponseDto<ProductAttributeResponseDto> page =
                new PageResponseDto<>(List.of(dto), 0, 10, 1, 1);

        when(service.getPagedAttributes(req)).thenReturn(page);

        mockMvc.perform(post("/api/product-attributes/paged")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].value").value("Red"));
    }

    @Test
    @DisplayName("GET /api/product-attributes/{id} — получить атрибут по ID")
    @WithMockUser(roles = "ADMIN")
    void testGetById() throws Exception {
        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("Test")
                .build();

        when(service.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/product-attributes/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("Test"));
    }

    @Test
    @DisplayName("GET /api/product-attributes/product/{productId} — получить атрибуты товара")
    @WithMockUser(roles = "ADMIN")
    void testGetByProductId() throws Exception {
        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("XL")
                .productId(5L)
                .build();

        when(service.getAllByProductId(5L)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/product-attributes/product/{productId}", 5L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].value").value("XL"));
    }

    @Test
    @DisplayName("POST /api/product-attributes — создать атрибут")
    @WithMockUser(roles = "ADMIN")
    void testCreate() throws Exception {
        ProductAttributeCreateDto req = ProductAttributeCreateDto.builder()
                .value("Blue")
                .categoryAttributeId(1L)
                .productId(5L)
                .build();

        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(10L)
                .value("Blue")
                .build();

        when(service.create(req)).thenReturn(dto);

        mockMvc.perform(post("/api/product-attributes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("Blue"));
    }

    @Test
    @DisplayName("PUT /api/product-attributes/{id} — обновить атрибут")
    @WithMockUser(roles = "ADMIN")
    void testUpdate() throws Exception {
        ProductAttributeUpdateDto req = ProductAttributeUpdateDto.builder()
                .value("Updated")
                .categoryAttributeId(2L)
                .productId(8L)
                .build();

        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("Updated")
                .build();

        when(service.update(1L, req)).thenReturn(dto);

        mockMvc.perform(put("/api/product-attributes/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.value").value("Updated"));
    }

    @Test
    @DisplayName("DELETE /api/product-attributes/{id} — удалить атрибут")
    @WithMockUser(roles = "ADMIN")
    void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/product-attributes/{id}", 1L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1L);
    }
}
