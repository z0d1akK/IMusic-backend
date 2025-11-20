package imusic.backend.service.ref;
import imusic.backend.dto.create.ref.ProductCategoryCreateDto;
import imusic.backend.dto.request.ref.ProductCategoryRequestDto;
import imusic.backend.dto.response.ref.ProductCategoryResponseDto;
import imusic.backend.dto.update.ref.ProductCategoryUpdateDto;
import imusic.backend.entity.ref.ProductCategory;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.ProductCategoryMapper;
import imusic.backend.repository.ref.ProductCategoryRepository;
import imusic.backend.service.impl.ref.ProductCategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductCategoryServiceTest {

    @Mock
    private ProductCategoryRepository repository;

    @Mock
    private ProductCategoryMapper mapper;

    @InjectMocks
    private ProductCategoryServiceImpl service;

    private ProductCategory entity;
    private ProductCategoryResponseDto responseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        entity = ProductCategory.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        responseDto = ProductCategoryResponseDto.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();
    }

    @Test
    @DisplayName("Get all product categories")
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<ProductCategoryResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get product category by ID")
    void testGetById() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        ProductCategoryResponseDto result = service.getById(1L);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get by invalid ID should throw exception")
    void testGetByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.getById(1L));
        assertTrue(exception.getMessage().contains("Категория продукта не найдена"));
    }

    @Test
    @DisplayName("Create new product category")
    void testCreate() {
        ProductCategoryCreateDto dto = ProductCategoryCreateDto.builder()
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        ProductCategoryResponseDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update product category")
    void testUpdate() {
        ProductCategoryUpdateDto dto = ProductCategoryUpdateDto.builder()
                .code("NEW_CODE")
                .name("New Name")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(
                ProductCategoryResponseDto.builder()
                        .id(1L)
                        .code("NEW_CODE")
                        .name("New Name")
                        .build()
        );

        ProductCategoryResponseDto result = service.update(1L, dto);

        assertNotNull(result);
        assertEquals("NEW_CODE", result.getCode());
        assertEquals("New Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update non-existent product category should throw exception")
    void testUpdateNotFound() {
        ProductCategoryUpdateDto dto = new ProductCategoryUpdateDto();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.update(1L, dto));
        assertTrue(exception.getMessage().contains("Категория продукта не найдена"));
    }

    @Test
    @DisplayName("Delete product category")
    void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("Delete non-existent product category should throw exception")
    void testDeleteNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.delete(1L));
        assertTrue(exception.getMessage().contains("Категория продукта не найдена"));
    }

    @Test
    @DisplayName("Get categories with filters")
    void testGetStatusesWithFilters() {
        ProductCategoryRequestDto request = ProductCategoryRequestDto.builder()
                .page(0)
                .size(10)
                .code("TEST")
                .name("Test")
                .sortBy("name")
                .sortDirection("asc")
                .build();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<ProductCategoryResponseDto> result = service.getCategoriesWithFilters(request);

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }
}
