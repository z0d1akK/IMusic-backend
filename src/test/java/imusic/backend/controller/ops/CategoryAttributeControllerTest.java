package imusic.backend.controller.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.create.ops.CategoryAttributeCreateDto;
import imusic.backend.dto.update.ops.CategoryAttributeUpdateDto;
import imusic.backend.dto.response.ops.CategoryAttributeResponseDto;
import imusic.backend.service.ops.CategoryAttributeService;
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

@WebMvcTest(CategoryAttributeController.class)
class CategoryAttributeControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;

    @MockBean private JwtService jwtService;
    @MockBean private CategoryAttributeService service;

    @Test
    @DisplayName("GET /api/category-attributes — возвращает все атрибуты")
    @WithMockUser
    void testGetAll() throws Exception {
        CategoryAttributeResponseDto dto = new CategoryAttributeResponseDto();
        dto.setId(1L);

        when(service.getAll()).thenReturn(List.of(dto));

        mvc.perform(get("/api/category-attributes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("GET /api/category-attributes/{id} — возвращает атрибут по ID")
    @WithMockUser
    void testGetById() throws Exception {
        CategoryAttributeResponseDto dto = new CategoryAttributeResponseDto();
        dto.setId(2L);

        when(service.getById(2L)).thenReturn(dto);

        mvc.perform(get("/api/category-attributes/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @DisplayName("GET /api/category-attributes/category/{categoryId} — возвращает атрибуты категории")
    @WithMockUser
    void testGetByCategory() throws Exception {
        CategoryAttributeResponseDto dto = new CategoryAttributeResponseDto();
        dto.setId(3L);

        when(service.getByCategoryId(10L)).thenReturn(List.of(dto));

        mvc.perform(get("/api/category-attributes/category/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(3L));
    }

    @Test
    @DisplayName("POST /api/category-attributes — создаёт атрибут")
    @WithMockUser(roles = "ADMIN")
    void testCreate() throws Exception {
        CategoryAttributeCreateDto dto = new CategoryAttributeCreateDto();
        dto.setName("Test");
        dto.setCategoryId(1L);
        dto.setDefaultValue("default");

        CategoryAttributeResponseDto response = new CategoryAttributeResponseDto();
        response.setId(5L);

        when(service.create(any())).thenReturn(response);

        mvc.perform(post("/api/category-attributes")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    @DisplayName("PUT /api/category-attributes/{id} — обновляет атрибут")
    @WithMockUser(roles = "MANAGER")
    void testUpdate() throws Exception {
        CategoryAttributeUpdateDto dto = new CategoryAttributeUpdateDto();
        dto.setName("Updated");
        dto.setCategoryId(1L);
        dto.setDefaultValue("default");

        CategoryAttributeResponseDto response = new CategoryAttributeResponseDto();
        response.setId(10L);

        when(service.update(eq(10L), any())).thenReturn(response);

        mvc.perform(put("/api/category-attributes/10")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("DELETE /api/category-attributes/{id} — удаляет атрибут")
    @WithMockUser(roles = "ADMIN")
    void testDelete() throws Exception {
        doNothing().when(service).delete(15L);

        mvc.perform(delete("/api/category-attributes/15")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(service).delete(15L);
    }
}
