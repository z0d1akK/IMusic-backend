package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.ProductAttributeCreateDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;
import imusic.backend.dto.update.ops.ProductAttributeUpdateDto;
import imusic.backend.entity.ops.ProductAttribute;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.ProductAttributeMapper;
import imusic.backend.mapper.resolver.ops.CategoryAttributeResolver;
import imusic.backend.mapper.resolver.ops.ProductResolver;
import imusic.backend.repository.ops.ProductAttributeRepository;
import imusic.backend.service.impl.ops.ProductAttributeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductAttributeServiceImplTest {

    @Mock
    private ProductAttributeRepository repository;

    @Mock
    private ProductAttributeMapper mapper;

    @Mock
    private CategoryAttributeResolver categoryAttributeResolver;

    @Mock
    private ProductResolver productResolver;

    @InjectMocks
    private ProductAttributeServiceImpl service;

    @Test
    @DisplayName("getAll() — должен возвращать список всех атрибутов")
    void testGetAll() {
        ProductAttribute entity = ProductAttribute.builder()
                .id(1L)
                .value("Red")
                .build();

        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("Red")
                .build();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(dto);

        List<ProductAttributeResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("Red", result.get(0).getValue());
    }

    @Test
    @DisplayName("getById() — найденный атрибут возвращается корректно")
    void testGetById() {
        ProductAttribute entity = ProductAttribute.builder()
                .id(10L)
                .value("XL")
                .build();

        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(10L)
                .value("XL")
                .build();

        when(repository.findById(10L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(dto);

        ProductAttributeResponseDto result = service.getById(10L);

        assertEquals("XL", result.getValue());
    }

    @Test
    @DisplayName("getById() — если нет атрибута, выбрасывается AppException")
    void testGetById_NotFound() {
        when(repository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> service.getById(100L));
    }

    @Test
    @DisplayName("getAllByProductId() — возвращает список атрибутов по продукту")
    void testGetAllByProductId() {
        ProductAttribute entity = ProductAttribute.builder()
                .id(1L)
                .value("Green")
                .build();

        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("Green")
                .build();

        when(repository.findAllByProduct_Id(5L)).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(dto);

        List<ProductAttributeResponseDto> result = service.getAllByProductId(5L);

        assertEquals(1, result.size());
        assertEquals("Green", result.get(0).getValue());
    }

    @Test
    @DisplayName("create() — успешно создает новый атрибут")
    void testCreate() {
        ProductAttributeCreateDto createDto = ProductAttributeCreateDto.builder()
                .value("Black")
                .productId(5L)
                .categoryAttributeId(2L)
                .build();

        ProductAttribute entity = ProductAttribute.builder()
                .id(1L)
                .value("Black")
                .build();

        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("Black")
                .build();

        when(mapper.toEntity(createDto, categoryAttributeResolver, productResolver)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(dto);

        ProductAttributeResponseDto result = service.create(createDto);

        assertEquals("Black", result.getValue());
    }

    @Test
    @DisplayName("update() — обновление существующего атрибута")
    void testUpdate() {
        ProductAttribute entity = ProductAttribute.builder()
                .id(1L)
                .value("Old")
                .build();

        ProductAttributeUpdateDto updateDto = ProductAttributeUpdateDto.builder()
                .value("New")
                .productId(5L)
                .categoryAttributeId(10L)
                .build();

        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("New")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(mapper).updateEntity(updateDto, entity, categoryAttributeResolver, productResolver);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(dto);

        ProductAttributeResponseDto result = service.update(1L, updateDto);

        assertEquals("New", result.getValue());
    }

    @Test
    @DisplayName("update() — выбрасывает ошибку, если атрибут не найден")
    void testUpdate_NotFound() {
        when(repository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> service.update(100L, new ProductAttributeUpdateDto()));
    }

    @Test
    @DisplayName("delete() — удаляет атрибут")
    void testDelete() {
        ProductAttribute entity = ProductAttribute.builder()
                .id(1L)
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        doNothing().when(repository).delete(entity);

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("delete() — выбрасывает ошибку, если атрибут не найден")
    void testDelete_NotFound() {
        when(repository.findById(200L)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> service.delete(200L));
    }

    @Test
    @DisplayName("getByProductId() — возвращает defaultValue, если value пустое")
    void testGetByProductId_DefaultValueApplied() {
        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value(null)
                .defaultValue("DEF")
                .build();

        when(repository.findAllByProduct_Id(5L)).thenReturn(List.of(
                ProductAttribute.builder().id(1L).build()
        ));

        when(mapper.toResponse(any())).thenReturn(dto);

        List<ProductAttributeResponseDto> result = service.getByProductId(5L);

        assertEquals("DEF", result.get(0).getValue());
    }

    @Test
    @DisplayName("updateOrCreate() — обновляет существующий атрибут")
    void testUpdateOrCreate_Update() {
        ProductAttribute entity = ProductAttribute.builder()
                .id(1L)
                .value("Old")
                .build();

        ProductAttributeUpdateDto updateDto = ProductAttributeUpdateDto.builder()
                .value("New")
                .productId(10L)
                .categoryAttributeId(5L)
                .build();

        ProductAttributeResponseDto responseDto = ProductAttributeResponseDto.builder()
                .id(1L)
                .value("New")
                .build();

        when(repository.findByProduct_IdAndCategoryAttribute_Id(10L, 5L))
                .thenReturn(Optional.of(entity));

        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        ProductAttributeResponseDto result = service.updateOrCreate(updateDto);

        assertEquals("New", result.getValue());
    }

    @Test
    @DisplayName("updateOrCreate() — создает новый атрибут, если старый не найден")
    void testUpdateOrCreate_Create() {
        ProductAttributeUpdateDto updateDto = ProductAttributeUpdateDto.builder()
                .value("NewVal")
                .productId(10L)
                .categoryAttributeId(5L)
                .build();

        ProductAttributeCreateDto createDto = ProductAttributeCreateDto.builder()
                .value("NewVal")
                .productId(10L)
                .categoryAttributeId(5L)
                .build();

        ProductAttribute entity = ProductAttribute.builder()
                .id(99L)
                .value("NewVal")
                .build();

        ProductAttributeResponseDto dto = ProductAttributeResponseDto.builder()
                .id(99L)
                .value("NewVal")
                .build();

        when(repository.findByProduct_IdAndCategoryAttribute_Id(10L, 5L))
                .thenReturn(Optional.empty());

        when(mapper.toEntity(any(ProductAttributeCreateDto.class),
                eq(categoryAttributeResolver), eq(productResolver))).thenReturn(entity);

        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(dto);

        ProductAttributeResponseDto result = service.updateOrCreate(updateDto);

        assertEquals("NewVal", result.getValue());
    }
}
