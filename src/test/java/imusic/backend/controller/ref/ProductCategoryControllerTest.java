package imusic.backend.controller.ref;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.create.ref.ProductCategoryCreateDto;
import imusic.backend.dto.request.ref.ProductCategoryRequestDto;
import imusic.backend.dto.response.ref.ProductCategoryResponseDto;
import imusic.backend.dto.update.ref.ProductCategoryUpdateDto;
import imusic.backend.service.ref.ProductCategoryService;
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

@WebMvcTest(ProductCategoryController.class)
class ProductCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtService tokenService;

    @MockBean
    private ProductCategoryService service;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/ref/product-categories - success for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testGetAll() throws Exception {
        ProductCategoryResponseDto dto = new ProductCategoryResponseDto(1L, "TEST_CODE", "Test Name");
        when(service.getAll()).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/ref/product-categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("TEST_CODE"));

        verify(service, times(1)).getAll();
    }

    @Test
    @DisplayName("GET /api/ref/product-categories/{id} - success for MANAGER")
    @WithMockUser(roles = "MANAGER")
    void testGetById() throws Exception {
        ProductCategoryResponseDto dto = new ProductCategoryResponseDto(1L, "TEST_CODE", "Test Name");
        when(service.getById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/ref/product-categories/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("TEST_CODE"));

        verify(service, times(1)).getById(1L);
    }

    @Test
    @DisplayName("POST /api/ref/product-categories - create for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testCreate() throws Exception {
        ProductCategoryCreateDto createDto = new ProductCategoryCreateDto();
        createDto.setCode("TEST_CODE");
        createDto.setName("Test Name");

        ProductCategoryResponseDto responseDto = new ProductCategoryResponseDto(1L, "TEST_CODE", "Test Name");
        when(service.create(createDto)).thenReturn(responseDto);

        mockMvc.perform(post("/api/ref/product-categories")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Name"));

        verify(service, times(1)).create(createDto);
    }

    @Test
    @DisplayName("PUT /api/ref/product-categories/{id} - update for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testUpdate() throws Exception {
        ProductCategoryUpdateDto updateDto = new ProductCategoryUpdateDto();
        updateDto.setCode("NEW_CODE");
        updateDto.setName("New Name");

        ProductCategoryResponseDto responseDto = new ProductCategoryResponseDto(1L, "NEW_CODE", "New Name");
        when(service.update(1L, updateDto)).thenReturn(responseDto);

        mockMvc.perform(put("/api/ref/product-categories/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("NEW_CODE"))
                .andExpect(jsonPath("$.name").value("New Name"));

        verify(service, times(1)).update(1L, updateDto);
    }

    @Test
    @DisplayName("DELETE /api/ref/product-categories/{id} - delete for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testDelete() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/ref/product-categories/{id}", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(service, times(1)).delete(1L);
    }

    @Test
    @DisplayName("POST /api/ref/product-categories/filter - success for ADMIN")
    @WithMockUser(roles = "ADMIN")
    void testGetWithFilters() throws Exception {
        ProductCategoryRequestDto request = new ProductCategoryRequestDto();
        request.setPage(0);
        request.setSize(10);
        request.setCode("TEST");
        request.setName("Test");

        ProductCategoryResponseDto dto = new ProductCategoryResponseDto(1L, "TEST_CODE", "Test Name");
        when(service.getCategoriesWithFilters(request)).thenReturn(List.of(dto));

        mockMvc.perform(post("/api/ref/product-categories/filter")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("TEST_CODE"));

        verify(service, times(1)).getCategoriesWithFilters(request);
    }
}
