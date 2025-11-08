package imusic.backend.service.impl.ops;

import imusic.backend.dto.create.ops.InventoryMovementCreateDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.update.ops.InventoryMovementUpdateDto;
import imusic.backend.dto.request.ops.InventoryMovementRequestDto;
import imusic.backend.dto.response.ops.InventoryMovementResponseDto;
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
import imusic.backend.service.ops.InventoryMovementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryMovementServiceImpl implements InventoryMovementService {

    private final InventoryMovementRepository movementRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final InventoryMovementTypeRepository inventoryMovementTypeRepository;
    private final InventoryMovementMapper movementMapper;
    private final ProductResolver productResolver;
    private final InventoryMovementTypeResolver movementTypeResolver;
    private final AuthService authService;
    private final UserResolver userResolver;
    private final UserMapper userMapper;
    private final RoleResolver roleResolver;
    private final UserStatusResolver userStatusResolver;

    @Override
    @Cacheable(cacheNames = "inventoryMovements", key = "'all'")
    public List<InventoryMovementResponseDto> getAll() {
        return movementRepository.findAll().stream()
                .map(movementMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "inventoryMovements", key = "#id")
    public InventoryMovementResponseDto getById(Long id) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new AppException("Движение товара не найдено, ID: " + id));
        return movementMapper.toResponse(movement);
    }

    @Override
    @CachePut(cacheNames = "inventoryMovements", key = "#result.id")
    @CacheEvict(cacheNames = "inventoryMovements", allEntries = true)
    public InventoryMovementResponseDto create(InventoryMovementCreateDto dto) {
        InventoryMovement movement = movementMapper.toEntity(dto, productResolver, movementTypeResolver, userResolver);
        Product product = movement.getProduct();

        movement.setCreatedBy(userMapper.responseToEntity(authService.getCurrentUser(), roleResolver, userStatusResolver));

        applyStockChange(movement);

        movementRepository.save(movement);
        productRepository.save(product);

        return movementMapper.toResponse(movement);
    }

    @Override
    @CacheEvict(cacheNames = "inventoryMovement", allEntries = true)
    public void createInventoryMovement(Product product, int quantity, String movementCode, String comment) {
        InventoryMovementType type = inventoryMovementTypeRepository.findByCode(movementCode)
                .orElseThrow(() -> new EntityNotFoundException("Тип передвижения товара не найден, код: " + movementCode));
        User systemUser = userRepository.findByUsername("system")
                .orElseThrow(() -> new EntityNotFoundException("Системный пользователь не найден"));

        InventoryMovement movement = InventoryMovement.builder()
                .product(product)
                .quantity(quantity)
                .movementType(type)
                .createdBy(systemUser)
                .createdAt(LocalDateTime.now())
                .comment(comment)
                .build();

        applyStockChange(movement);

        movementRepository.save(movement);
        productRepository.save(product);
    }

    @Override
    @CacheEvict(cacheNames = "inventoryMovements", allEntries = true)
    public InventoryMovementResponseDto update(Long id, InventoryMovementUpdateDto dto) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new AppException("Движение товара не найдено, ID: " + id));

        rollbackStockChange(movement);

        movementMapper.updateEntity(dto, movement, productResolver, movementTypeResolver);

        applyStockChange(movement);

        movementRepository.save(movement);
        productRepository.save(movement.getProduct());

        return movementMapper.toResponse(movement);
    }

    @Override
    @CacheEvict(cacheNames = "inventoryMovements", allEntries = true)
    public void delete(Long id) {
        InventoryMovement movement = movementRepository.findById(id)
                .orElseThrow(() -> new AppException("Движение товара не найдено, ID: " + id));

        rollbackStockChange(movement);

        movementRepository.delete(movement);
        productRepository.save(movement.getProduct());
    }

    @Override
    public PageResponseDto<InventoryMovementResponseDto> getPagedMovements(InventoryMovementRequestDto request) {
        List<InventoryMovement> movements = movementRepository.findAll().stream()
                .filter(m -> request.getProductId() == null || m.getProduct().getId().equals(request.getProductId()))
                .filter(m -> request.getMovementTypeId() == null || m.getMovementType().getId().equals(request.getMovementTypeId()))
                .filter(m -> request.getCreatedById() == null || m.getCreatedBy().getId().equals(request.getCreatedById()))
                .collect(Collectors.toList());

        movements.sort(getMovementSortComparator(request.getSortBy(), request.getSortDirection()));

        int totalElements = movements.size();
        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), totalElements);

        List<InventoryMovementResponseDto> content = movements.subList(fromIndex, toIndex)
                .stream()
                .map(movementMapper::toResponse)
                .toList();

        return new PageResponseDto<>(content, request.getPage(), request.getSize(), totalElements, totalPages);
    }

    private Comparator<InventoryMovement> getMovementSortComparator(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        Comparator<InventoryMovement> comparator = switch (sortBy) {
            case "quantity" -> Comparator.comparing(InventoryMovement::getQuantity, Comparator.nullsLast(Integer::compareTo));
            case "createdAt" -> Comparator.comparing(InventoryMovement::getCreatedAt, Comparator.nullsLast(LocalDateTime::compareTo));
            default -> Comparator.comparing(InventoryMovement::getId);
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }


    private void applyStockChange(InventoryMovement movement) {
        String typeCode = movement.getMovementType().getCode();
        int quantity = movement.getQuantity();
        Product product = movement.getProduct();

        switch (typeCode) {
            case "INCOME" -> {
                product.setWarehouseQuantity(product.getWarehouseQuantity() + quantity);
                product.setStockQuantity(product.getStockQuantity() + quantity);
            }
            case "OUTCOME" -> {
                if (product.getWarehouseQuantity() < quantity) {
                    throw new IllegalArgumentException("Недостаточно товара на складе");
                }
                product.setWarehouseQuantity(product.getWarehouseQuantity() - quantity);
                product.setStockQuantity(product.getStockQuantity() - quantity);
            }
            case "RESERVE_IN_CART" -> {
                if (product.getStockQuantity() < quantity) {
                    throw new IllegalArgumentException("Недостаточно товара для резервирования в корзине");
                }
                product.setStockQuantity(product.getStockQuantity() - quantity);
            }
            case "RETURN_TO_STOCK" -> {
                product.setStockQuantity(product.getStockQuantity() + quantity);
            }
            case "ADJUSTMENT" -> {
                product.setWarehouseQuantity(quantity);
                product.setStockQuantity(quantity);
            }
            default -> throw new IllegalArgumentException("Неизвестный тип движения товара: " + typeCode);
        }
    }

    private void rollbackStockChange(InventoryMovement movement) {
        String typeCode = movement.getMovementType().getCode();
        int quantity = movement.getQuantity();
        Product product = movement.getProduct();

        switch (typeCode) {
            case "INCOME" -> product.setWarehouseQuantity(product.getWarehouseQuantity() - quantity);
            case "OUTCOME" -> product.setWarehouseQuantity(product.getWarehouseQuantity() + quantity);
            case "RESERVE_IN_CART" -> product.setStockQuantity(product.getStockQuantity() + quantity);
            case "RETURN_TO_STOCK", "CANCELLED" -> product.setStockQuantity(product.getStockQuantity() - quantity);
            case "RETURNED" -> {
                product.setWarehouseQuantity(product.getWarehouseQuantity() - quantity);
                product.setStockQuantity(product.getStockQuantity() - quantity);
            }
            case "ADJUSTMENT" -> {}
            default -> throw new IllegalArgumentException("Неизвестный тип движения товара: " + typeCode);
        }
    }
}
