package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.ProductCreateDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.entity.ops.Product;
import imusic.backend.entity.ref.ProductCategory;
import imusic.backend.entity.ref.ProductUnit;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.ProductMapper;
import imusic.backend.repository.ops.ProductRepository;
import imusic.backend.service.impl.ops.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CategoryAttributeService categoryAttributeService;

    @Mock
    private ProductAttributeService productCategoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    private ProductCategory category;
    private ProductUnit unit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        category = ProductCategory.builder()
                .id(1L)
                .name("Electronics")
                .build();

        unit = ProductUnit.builder()
                .id(1L)
                .name("Piece")
                .build();
    }

    @Test
    void testGetAllProducts() {
        Product product = Product.builder()
                .id(1L)
                .name("Smartphone")
                .description("Latest smartphone")
                .price(699.99f)
                .category(category)
                .unit(unit)
                .stockQuantity(50)
                .warehouseQuantity(100)
                .createdAt(LocalDateTime.now())
                .build();

        ProductResponseDto dto = ProductResponseDto.builder()
                .id(1L)
                .name("Smartphone")
                .price(699.99f)
                .categoryId(1L)
                .categoryName("Electronics")
                .unitId(1L)
                .unitName("Piece")
                .stockQuantity(50)
                .warehouseQuantity(100)
                .createdAt(product.getCreatedAt())
                .build();

        when(productRepository.findAll()).thenReturn(List.of(product));
        when(productMapper.toResponse(product)).thenReturn(dto);

        List<ProductResponseDto> result = productService.getAll();

        assertEquals(1, result.size());
        assertEquals("Smartphone", result.get(0).getName());
        verify(productRepository, times(1)).findAll();
    }

    @Test
    void testGetById_ProductExists() {
        Product product = Product.builder()
                .id(1L)
                .name("Laptop")
                .price(1499.99f)
                .category(category)
                .unit(unit)
                .createdAt(LocalDateTime.now())
                .build();

        ProductResponseDto dto = ProductResponseDto.builder()
                .id(1L)
                .name("Laptop")
                .price(1499.99f)
                .categoryId(1L)
                .categoryName("Electronics")
                .unitId(1L)
                .unitName("Piece")
                .createdAt(product.getCreatedAt())
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toResponse(product)).thenReturn(dto);

        ProductResponseDto result = productService.getById(1L);

        assertEquals("Laptop", result.getName());
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetById_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> productService.getById(1L));
        assertTrue(exception.getMessage().contains("Товар не найден"));
    }

    @Test
    void testCreateProduct() {
        ProductCreateDto dto = ProductCreateDto.builder()
                .name("Headphones")
                .price(199.99f)
                .categoryId(category.getId())
                .unitId(1L)
                .stockQuantity(40)
                .warehouseQuantity(50)
                .build();

        Product productEntity = Product.builder()
                .id(1L)
                .name(dto.getName())
                .price(dto.getPrice())
                .category(category)
                .unit(unit)
                .stockQuantity(dto.getStockQuantity())
                .warehouseQuantity(dto.getWarehouseQuantity())
                .createdAt(LocalDateTime.now())
                .build();

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(1L)
                .name("Headphones")
                .price(199.99f)
                .categoryId(1L)
                .categoryName("Electronics")
                .unitId(1L)
                .unitName("Piece")
                .stockQuantity(40)
                .warehouseQuantity(50)
                .createdAt(productEntity.getCreatedAt())
                .build();

        when(productMapper.toEntity(dto, null, null)).thenReturn(productEntity);
        when(productRepository.save(productEntity)).thenReturn(productEntity);
        when(productMapper.toResponse(productEntity)).thenReturn(responseDto);

        ProductResponseDto result = productService.create(dto);

        assertEquals("Headphones", result.getName());
        verify(productRepository).save(productEntity);
    }
}
