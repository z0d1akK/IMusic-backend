package imusic.backend.controller.ops;

import imusic.backend.dto.create.ops.ProductCreateDto;
import imusic.backend.dto.request.ops.ProductRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.*;
import imusic.backend.dto.update.ops.ProductUpdateDto;
import imusic.backend.service.ops.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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

        assertEquals(1, Objects.requireNonNull(response.getBody()).size());
        assertEquals("Smartphone", response.getBody().getFirst().getName());
        verify(productService).getAll();
    }

    @Test
    @DisplayName("Get paged products should return page")
    void testGetPagedProducts() {
        ProductRequestDto request = new ProductRequestDto();

        PageResponseDto<ProductResponseDto> page =
                new PageResponseDto<>(List.of(), 0, 10, 0, 0);

        when(productService.getPagedProducts(request)).thenReturn(page);

        ResponseEntity<PageResponseDto<ProductResponseDto>> response =
                productController.getPagedProducts(request);

        assertNotNull(response.getBody());
        verify(productService).getPagedProducts(request);
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

        assertEquals("Laptop", Objects.requireNonNull(response.getBody()).getName());
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

        assertEquals("Headphones", Objects.requireNonNull(response.getBody()).getName());
        assertEquals(199.99f, response.getBody().getPrice());
        verify(productService).create(dto);
    }

    @Test
    @DisplayName("Update product should return updated product")
    void testUpdateProduct() {
        ProductUpdateDto dto = new ProductUpdateDto();

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(1L)
                .name("Updated")
                .build();

        when(productService.update(1L, dto)).thenReturn(responseDto);

        ResponseEntity<ProductResponseDto> response =
                productController.updateProduct(1L, dto);

        assertEquals("Updated", Objects.requireNonNull(response.getBody()).getName());
        verify(productService).update(1L, dto);
    }

    @Test
    @DisplayName("Delete product should call service")
    void testDeleteProduct() {

        ResponseEntity<Void> response =
                productController.deleteProduct(1L);

        assertEquals(204, response.getStatusCodeValue());
        verify(productService).delete(1L);
    }

    @Test
    @DisplayName("Upload product image should call service")
    void testUploadImage() {

        MultipartFile file = mock(MultipartFile.class);

        ResponseEntity<Void> response =
                productController.uploadProductImage(1L, file);

        assertEquals(200, response.getStatusCodeValue());
        verify(productService).uploadProductImage(1L, file);
    }

    @Test
    @DisplayName("Get product attributes")
    void testGetAttributes() {

        when(productService.getAttributesByProductId(1L))
                .thenReturn(List.of());

        ResponseEntity<List<ProductAttributeResponseDto>> response =
                productController.getProductAttributes(1L);

        assertNotNull(response.getBody());
        verify(productService).getAttributesByProductId(1L);
    }

    @Test
    @DisplayName("Get product attributes with values")
    void testGetAttributesWithValues() {

        when(productService.getCategoryAttributesWithValues(1L))
                .thenReturn(List.of());

        ResponseEntity<List<CategoryAttributeResponseDto>> response =
                productController.getProductAttributesWithValues(1L);

        assertNotNull(response.getBody());
        verify(productService).getCategoryAttributesWithValues(1L);
    }

    @Test
    @DisplayName("Create comparison")
    void testCreateComparison() {

        when(productService.createComparison(List.of(1L, 2L)))
                .thenReturn(100L);

        ResponseEntity<Long> response =
                productController.createComparison(List.of(1L, 2L));

        assertEquals(100L, response.getBody());
        verify(productService).createComparison(List.of(1L, 2L));
    }

    @Test
    @DisplayName("Get comparison")
    void testGetComparison() {

        ProductComparisonResponseDto dto = new ProductComparisonResponseDto();

        when(productService.getComparison(1L)).thenReturn(dto);

        ResponseEntity<ProductComparisonResponseDto> response =
                productController.getComparison(1L);

        assertNotNull(response.getBody());
        verify(productService).getComparison(1L);
    }

    @Test
    @DisplayName("Get user comparisons")
    void testGetUserComparisons() {

        when(productService.getUserComparisons())
                .thenReturn(List.of());

        ResponseEntity<List<ComparisonResponseDto>> response =
                productController.getUserComparisons();

        assertNotNull(response.getBody());
        verify(productService).getUserComparisons();
    }

    @Test
    @DisplayName("Add product to comparison")
    void testAddProductToComparison() {

        ResponseEntity<Void> response =
                productController.addProduct(1L, 2L);

        assertEquals(200, response.getStatusCodeValue());
        verify(productService).addProductToComparison(1L, 2L);
    }

    @Test
    @DisplayName("Remove product from comparison")
    void testRemoveProductFromComparison() {

        ResponseEntity<Void> response =
                productController.removeProduct(1L, 2L);

        assertEquals(204, response.getStatusCodeValue());
        verify(productService).removeProductFromComparison(1L, 2L);
    }

    @Test
    @DisplayName("Get price history")
    void testGetPriceHistory() {

        when(productService.getPriceHistory(1L, 2))
                .thenReturn(List.of());

        ResponseEntity<List<PriceHistoryPointDto>> response =
                productController.getPriceHistory(1L, 2);

        assertNotNull(response.getBody());
        verify(productService).getPriceHistory(1L, 2);
    }
}
