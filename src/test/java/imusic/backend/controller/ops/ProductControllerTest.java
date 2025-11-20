package imusic.backend.controller.ops;

import imusic.backend.dto.create.ops.ProductCreateDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.service.ops.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Get all products should return list of products")
    void testGetAllProducts() {
        ProductResponseDto product = ProductResponseDto.builder()
                .id(1L)
                .name("Smartphone")
                .description("Latest smartphone")
                .price(699.99f)
                .categoryId(1L)
                .categoryName("Electronics")
                .unitId(1L)
                .unitName("Piece")
                .stockQuantity(50)
                .warehouseQuantity(100)
                .createdAt(LocalDateTime.now())
                .build();

        when(productService.getAll()).thenReturn(List.of(product));

        ResponseEntity<List<ProductResponseDto>> response = productController.getAllProducts();

        assertEquals(1, response.getBody().size());
        assertEquals("Smartphone", response.getBody().get(0).getName());
        verify(productService).getAll();
    }

    @Test
    @DisplayName("Get product by ID should return product")
    void testGetProductById() {
        ProductResponseDto product = ProductResponseDto.builder()
                .id(1L)
                .name("Laptop")
                .price(1499.99f)
                .build();

        when(productService.getById(1L)).thenReturn(product);

        ResponseEntity<ProductResponseDto> response = productController.getProductById(1L);

        assertEquals("Laptop", response.getBody().getName());
        assertEquals(1499.99f, response.getBody().getPrice());
        verify(productService).getById(1L);
    }

    @Test
    @DisplayName("Create product should return created product")
    void testCreateProduct() {
        ProductCreateDto dto = ProductCreateDto.builder()
                .name("Headphones")
                .price(199.99f)
                .categoryId(1L)
                .unitId(1L)
                .stockQuantity(40)
                .warehouseQuantity(50)
                .build();

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(1L)
                .name("Headphones")
                .price(199.99f)
                .build();

        when(productService.create(dto)).thenReturn(responseDto);

        ResponseEntity<ProductResponseDto> response = productController.createProduct(dto);

        assertEquals("Headphones", response.getBody().getName());
        assertEquals(199.99f, response.getBody().getPrice());
        verify(productService).create(dto);
    }
}
