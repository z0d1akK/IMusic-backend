package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.CategoryAttributeCreateDto;
import imusic.backend.dto.update.ops.CategoryAttributeUpdateDto;
import imusic.backend.dto.response.ops.CategoryAttributeResponseDto;
import imusic.backend.entity.ops.CategoryAttribute;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.CategoryAttributeMapper;
import imusic.backend.mapper.resolver.ref.ProductCategoryResolver;
import imusic.backend.repository.ops.CategoryAttributeRepository;
import imusic.backend.repository.ops.ProductAttributeRepository;
import imusic.backend.service.impl.ops.CategoryAttributeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryAttributeServiceImplTest {

    @InjectMocks
    private CategoryAttributeServiceImpl service;

    @Mock private CategoryAttributeRepository categoryAttributeRepository;
    @Mock private ProductAttributeRepository productAttributeRepository;
    @Mock private CategoryAttributeMapper categoryAttributeMapper;
    @Mock private ProductCategoryResolver productCategoryResolver;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getAll — возвращает список всех атрибутов")
    void testGetAll() {
        CategoryAttribute attr = new CategoryAttribute();
        attr.setId(1L);
        CategoryAttributeResponseDto dto = new CategoryAttributeResponseDto();
        dto.setId(1L);

        when(categoryAttributeRepository.findAll()).thenReturn(List.of(attr));
        when(categoryAttributeMapper.toResponse(attr)).thenReturn(dto);

        List<CategoryAttributeResponseDto> result = service.getAll();
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("getById — возвращает атрибут по ID")
    void testGetById() {
        CategoryAttribute attr = new CategoryAttribute();
        attr.setId(5L);
        CategoryAttributeResponseDto dto = new CategoryAttributeResponseDto();
        dto.setId(5L);

        when(categoryAttributeRepository.findById(5L)).thenReturn(Optional.of(attr));
        when(categoryAttributeMapper.toResponse(attr)).thenReturn(dto);

        CategoryAttributeResponseDto result = service.getById(5L);
        assertEquals(5L, result.getId());
    }

    @Test
    @DisplayName("getById — выбрасывает исключение, если не найден")
    void testGetByIdNotFound() {
        when(categoryAttributeRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> service.getById(10L));
    }

    @Test
    @DisplayName("getByCategoryId — возвращает атрибуты категории")
    void testGetByCategoryId() {
        CategoryAttribute attr = new CategoryAttribute();
        attr.setId(7L);
        CategoryAttributeResponseDto dto = new CategoryAttributeResponseDto();
        dto.setId(7L);

        when(categoryAttributeRepository.findByCategoryId(3L)).thenReturn(List.of(attr));
        when(categoryAttributeMapper.toResponse(attr)).thenReturn(dto);

        List<CategoryAttributeResponseDto> result = service.getByCategoryId(3L);
        assertEquals(1, result.size());
        assertEquals(7L, result.get(0).getId());
    }

    @Test
    @DisplayName("create — создаёт атрибут")
    void testCreate() {
        CategoryAttributeCreateDto dto = new CategoryAttributeCreateDto();
        dto.setName("TestAttr");
        dto.setCategoryId(2L);
        dto.setDefaultValue("default");

        CategoryAttribute entity = new CategoryAttribute();
        entity.setId(20L);

        CategoryAttributeResponseDto response = new CategoryAttributeResponseDto();
        response.setId(20L);

        when(categoryAttributeMapper.toEntity(dto, productCategoryResolver)).thenReturn(entity);
        when(categoryAttributeRepository.save(entity)).thenReturn(entity);
        when(categoryAttributeMapper.toResponse(entity)).thenReturn(response);

        CategoryAttributeResponseDto result = service.create(dto);
        assertEquals(20L, result.getId());
    }

    @Test
    @DisplayName("update — обновляет атрибут")
    void testUpdate() {
        CategoryAttributeUpdateDto dto = new CategoryAttributeUpdateDto();
        dto.setName("Updated");
        dto.setCategoryId(2L);
        dto.setDefaultValue("default");

        CategoryAttribute entity = new CategoryAttribute();
        entity.setId(30L);

        CategoryAttributeResponseDto response = new CategoryAttributeResponseDto();
        response.setId(30L);

        when(categoryAttributeRepository.findById(30L)).thenReturn(Optional.of(entity));
        doNothing().when(categoryAttributeMapper).updateEntity(dto, entity, productCategoryResolver);
        when(categoryAttributeRepository.save(entity)).thenReturn(entity);
        when(categoryAttributeMapper.toResponse(entity)).thenReturn(response);

        CategoryAttributeResponseDto result = service.update(30L, dto);
        assertEquals(30L, result.getId());
    }

    @Test
    @DisplayName("delete — удаляет атрибут и связанные productAttributes")
    void testDelete() {
        CategoryAttribute entity = new CategoryAttribute();
        entity.setId(40L);

        when(categoryAttributeRepository.findById(40L)).thenReturn(Optional.of(entity));

        service.delete(40L);

        verify(productAttributeRepository).deleteByCategoryAttributeId(40L);
        verify(categoryAttributeRepository).delete(entity);
    }
}
