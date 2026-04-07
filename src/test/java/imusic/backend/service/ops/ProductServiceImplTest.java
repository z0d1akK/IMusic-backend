package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.ProductCreateDto;
import imusic.backend.dto.response.ops.ComparisonResponseDto;
import imusic.backend.dto.response.ops.PriceHistoryPointDto;
import imusic.backend.dto.response.ops.ProductComparisonResponseDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.entity.ops.Comparison;
import imusic.backend.entity.ops.Product;
import imusic.backend.entity.ops.User;
import imusic.backend.entity.ops.ComparisonItem;
import imusic.backend.entity.ref.ProductCategory;
import imusic.backend.entity.ref.ProductUnit;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.ProductMapper;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.repository.ops.*;
import imusic.backend.service.auth.AuthService;
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

    @Mock private PriceHistoryRepository priceHistoryRepository;
    @Mock private ComparisonRepository comparisonRepository;
    @Mock private ComparisonItemRepository comparisonItemRepository;
    @Mock private InventoryMovementRepository inventoryMovementRepository;
    @Mock private OrderItemRepository orderItemRepository;
    @Mock private UserMapper userMapper;
    @Mock private AuthService authService;
    @Mock private RoleResolver roleResolver;
    @Mock private UserStatusResolver userStatusResolver;

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
        assertEquals("Smartphone", result.getFirst().getName());
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

    @Test
    void testCreateComparisonSuccess() {

        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(10L);

        Comparison comparison = new Comparison();
        comparison.setId(100L);

        when(authService.getCurrentUser()).thenReturn(null);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(comparisonRepository.save(any())).thenReturn(comparison);

        Long result = productService.createComparison(List.of(10L));

        assertEquals(100L, result);
        verify(comparisonItemRepository).save(any());
    }

    @Test
    void testCreateComparisonProductNotFound() {

        when(authService.getCurrentUser()).thenReturn(null);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(new User());
        when(productRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> productService.createComparison(List.of(10L)));
    }

    @Test
    void testGetComparisonSuccess() {

        Product product = new Product();
        product.setId(1L);

        ComparisonItem item = new ComparisonItem();
        item.setProduct(product);

        when(comparisonItemRepository.findAllByComparison_Id(1L))
                .thenReturn(List.of(item));

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        when(productMapper.toResponse(any()))
                .thenReturn(new ProductResponseDto());

        when(productCategoryService.getAll())
                .thenReturn(List.of());

        ProductComparisonResponseDto result = productService.getComparison(1L);

        assertNotNull(result);
    }

    @Test
    void testGetComparisonEmpty() {
        when(comparisonItemRepository.findAllByComparison_Id(1L))
                .thenReturn(List.of());

        assertThrows(AppException.class,
                () -> productService.getComparison(1L));
    }

    @Test
    void testAddProductToComparisonSuccess() {

        Comparison comparison = new Comparison();
        Product product = new Product();

        when(comparisonItemRepository.existsByComparison_IdAndProduct_Id(1L, 2L))
                .thenReturn(false);

        when(comparisonRepository.findById(1L))
                .thenReturn(Optional.of(comparison));

        when(productRepository.findById(2L))
                .thenReturn(Optional.of(product));

        productService.addProductToComparison(1L, 2L);

        verify(comparisonItemRepository).save(any());
    }

    @Test
    void testAddProductToComparisonDuplicate() {

        when(comparisonItemRepository.existsByComparison_IdAndProduct_Id(1L, 2L))
                .thenReturn(true);

        productService.addProductToComparison(1L, 2L);

        verify(comparisonItemRepository, never()).save(any());
    }

    @Test
    void testRemoveProductFromComparison() {

        productService.removeProductFromComparison(1L, 2L);

        verify(comparisonItemRepository)
                .deleteByComparison_IdAndProduct_Id(1L, 2L);
    }

    @Test
    void testGetUserComparisons() {

        User user = new User();
        user.setId(1L);

        Comparison comp = new Comparison();
        comp.setId(10L);
        comp.setCreatedAt(LocalDateTime.now());

        when(authService.getCurrentUser()).thenReturn(null);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);

        when(comparisonRepository.findAllByUserId(1L))
                .thenReturn(List.of(comp));

        when(comparisonItemRepository.countByComparisonId(10L))
                .thenReturn(2L);

        List<ComparisonResponseDto> result = productService.getUserComparisons();

        assertEquals(1, result.size());
        assertEquals(2, result.getFirst().getProductCount());
    }

    @Test
    void testGetPriceHistoryDaily() {

        when(priceHistoryRepository.getDailyPriceHistory(eq(1L), any(), any()))
                .thenReturn(List.<Object[]>of(new Object[]{"2024-01-01", 100}));

        List<PriceHistoryPointDto> result = productService.getPriceHistory(1L, 2);

        assertEquals(1, result.size());
        assertEquals(100f, result.getFirst().getPrice());
    }

    @Test
    void testGetPriceHistoryMonthly() {

        when(priceHistoryRepository.getMonthlyPriceHistory(eq(1L), any(), any()))
                .thenReturn(List.<Object[]>of(new Object[]{"Jan", 150}));

        List<PriceHistoryPointDto> result = productService.getPriceHistory(1L, 6);

        assertEquals(1, result.size());
        assertEquals(150f, result.getFirst().getPrice());
    }
    @Test
    void testGetPriceHistoryFallbackToCurrentPrice() {

        Product product = new Product();
        product.setId(1L);
        product.setPrice(200f);

        when(priceHistoryRepository.getDailyPriceHistory(eq(1L), any(), any()))
                .thenReturn(List.of());

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(product));

        List<PriceHistoryPointDto> result = productService.getPriceHistory(1L, 2);

        assertEquals(1, result.size());
        assertEquals(200f, result.getFirst().getPrice());
    }

    @Test
    void testGetPriceHistoryProductNotFound() {

        when(priceHistoryRepository.getDailyPriceHistory(eq(1L), any(), any()))
                .thenReturn(List.of());

        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(AppException.class,
                () -> productService.getPriceHistory(1L, 2));
    }
}
