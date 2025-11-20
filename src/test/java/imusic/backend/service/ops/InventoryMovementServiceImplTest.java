package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.InventoryMovementCreateDto;
import imusic.backend.dto.response.ops.InventoryMovementResponseDto;
import imusic.backend.dto.update.ops.InventoryMovementUpdateDto;
import imusic.backend.entity.ops.InventoryMovement;
import imusic.backend.entity.ops.Product;
import imusic.backend.entity.ops.User;
import imusic.backend.entity.ref.InventoryMovementType;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.InventoryMovementMapper;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ops.ProductResolver;
import imusic.backend.mapper.resolver.ops.UserResolver;
import imusic.backend.mapper.resolver.ref.InventoryMovementTypeResolver;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.repository.ops.InventoryMovementRepository;
import imusic.backend.repository.ops.ProductRepository;
import imusic.backend.repository.ops.UserRepository;
import imusic.backend.repository.ref.InventoryMovementTypeRepository;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.impl.ops.InventoryMovementServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

public class InventoryMovementServiceImplTest {

    @InjectMocks
    private InventoryMovementServiceImpl service;

    @Mock private InventoryMovementRepository movementRepository;
    @Mock private ProductRepository productRepository;
    @Mock private UserRepository userRepository;
    @Mock private InventoryMovementTypeRepository typeRepository;

    @Mock private InventoryMovementMapper movementMapper;
    @Mock private ProductResolver productResolver;
    @Mock private InventoryMovementTypeResolver typeResolver;
    @Mock private UserResolver userResolver;

    @Mock private AuthService authService;
    @Mock private UserMapper userMapper;
    @Mock private RoleResolver roleResolver;
    @Mock private UserStatusResolver userStatusResolver;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getAll — возвращает список DTO")
    void testGetAll() {
        InventoryMovement m = InventoryMovement.builder()
                .id(1L)
                .quantity(10)
                .build();

        InventoryMovementResponseDto dto = new InventoryMovementResponseDto();
        dto.setId(1L);

        when(movementRepository.findAll()).thenReturn(List.of(m));
        when(movementMapper.toResponse(m)).thenReturn(dto);

        List<InventoryMovementResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("getById — возвращает DTO")
    void testGetById() {
        InventoryMovement movement = InventoryMovement.builder()
                .id(5L)
                .quantity(3)
                .build();

        InventoryMovementResponseDto dto = new InventoryMovementResponseDto();
        dto.setId(5L);

        when(movementRepository.findById(5L)).thenReturn(Optional.of(movement));
        when(movementMapper.toResponse(movement)).thenReturn(dto);

        InventoryMovementResponseDto result = service.getById(5L);

        assertEquals(5L, result.getId());
    }

    @Test
    @DisplayName("getById — выбрасывает AppException, если не найдено")
    void testGetByIdNotFound() {
        when(movementRepository.findById(10L)).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> service.getById(10L));
    }

    @Test
    @DisplayName("create — создает движение и обновляет склад")
    void testCreate() {
        InventoryMovementCreateDto dto = new InventoryMovementCreateDto();
        dto.setProductId(1L);
        dto.setMovementTypeId(2L);
        dto.setQuantity(10);

        Product product = new Product();
        product.setId(1L);
        product.setWarehouseQuantity(5);
        product.setStockQuantity(5);

        InventoryMovementType type = new InventoryMovementType();
        type.setId(2L);
        type.setCode("INCOME");

        User currentUser = new User();
        currentUser.setId(100L);

        User createdByUser = new User();
        createdByUser.setId(100L);

        InventoryMovement entity = InventoryMovement.builder()
                .id(50L)
                .product(product)
                .movementType(type)
                .quantity(10)
                .createdBy(createdByUser)
                .createdAt(LocalDateTime.now())
                .build();

        InventoryMovementResponseDto response = new InventoryMovementResponseDto();
        response.setId(50L);

        when(movementMapper.toEntity(eq(dto), any(), any(), any()))
                .thenReturn(entity);
        when(authService.getCurrentUser()).thenReturn(null);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(createdByUser);
        when(movementRepository.save(entity)).thenReturn(entity);
        when(productRepository.save(product)).thenReturn(product);
        when(movementMapper.toResponse(entity)).thenReturn(response);

        InventoryMovementResponseDto result = service.create(dto);

        assertEquals(50L, result.getId());
        assertEquals(15, product.getWarehouseQuantity());
        assertEquals(15, product.getStockQuantity());
    }

    @Test
    @DisplayName("update — откатывает старые изменения и применяет новые")
    void testUpdate() {
        Product product = new Product();
        product.setId(1L);
        product.setWarehouseQuantity(50);
        product.setStockQuantity(50);

        InventoryMovementType oldType = new InventoryMovementType();
        oldType.setId(1L);
        oldType.setCode("OUTCOME");

        InventoryMovementType newType = new InventoryMovementType();
        newType.setId(2L);
        newType.setCode("INCOME");

        InventoryMovement existing = InventoryMovement.builder()
                .id(99L)
                .quantity(10)
                .movementType(oldType)
                .product(product)
                .build();

        InventoryMovementUpdateDto dto = new InventoryMovementUpdateDto();
        dto.setQuantity(20);
        dto.setMovementTypeId(2L);

        InventoryMovementResponseDto resultDto = new InventoryMovementResponseDto();
        resultDto.setId(99L);

        when(movementRepository.findById(99L)).thenReturn(Optional.of(existing));
        doAnswer(inv -> {
            existing.setQuantity(20);
            existing.setMovementType(newType);
            return null;
        }).when(movementMapper).updateEntity(eq(dto), eq(existing), any(), any());
        when(movementRepository.save(existing)).thenReturn(existing);
        when(productRepository.save(product)).thenReturn(product);
        when(movementMapper.toResponse(existing)).thenReturn(resultDto);

        InventoryMovementResponseDto result = service.update(99L, dto);

        assertEquals(99L, result.getId());
        assertEquals(80, product.getWarehouseQuantity());
        assertEquals(80, product.getStockQuantity());
    }

    @Test
    @DisplayName("delete — удаляет движение и откатывает склад")
    void testDelete() {
        InventoryMovementType type = new InventoryMovementType();
        type.setCode("INCOME");

        Product product = new Product();
        product.setWarehouseQuantity(20);
        product.setStockQuantity(20);

        InventoryMovement movement = InventoryMovement.builder()
                .id(7L)
                .quantity(5)
                .movementType(type)
                .product(product)
                .build();

        when(movementRepository.findById(7L)).thenReturn(Optional.of(movement));

        service.delete(7L);

        verify(movementRepository).delete(movement);
        verify(productRepository).save(product);

        assertEquals(15, product.getWarehouseQuantity());
        assertEquals(15, product.getStockQuantity());
    }
}
