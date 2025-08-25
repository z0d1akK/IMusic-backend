package imusic.backend.service.impl.ref;

import imusic.backend.dto.request.ref.InventoryMovementTypeRequestDto;
import imusic.backend.dto.response.ref.InventoryMovementTypeResponseDto;
import imusic.backend.dto.create.ref.InventoryMovementTypeCreateDto;
import imusic.backend.dto.update.ref.InventoryMovementTypeUpdateDto;
import imusic.backend.entity.ref.InventoryMovementType;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.InventoryMovementTypeMapper;
import imusic.backend.repository.ref.InventoryMovementTypeRepository;
import imusic.backend.service.ref.InventoryMovementTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class InventoryMovementTypeServiceImpl implements InventoryMovementTypeService {

    private final InventoryMovementTypeRepository repository;
    private final InventoryMovementTypeMapper mapper;

    @Override
    @Cacheable(cacheNames = "inventoryMovementTypes", key = "'all'")
    public List<InventoryMovementTypeResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "inventoryMovementTypes", key = "#id")
    public InventoryMovementTypeResponseDto getById(Long id) {
        InventoryMovementType type = repository.findById(id)
                .orElseThrow(() -> new AppException("Тип движения запасов не найден, id: " + id));
        return mapper.toResponse(type);
    }

    @Override
    @CacheEvict(cacheNames = "inventoryMovementTypes", allEntries = true)
    @CachePut(cacheNames = "inventoryMovementTypes", key = "#result.id")
    public InventoryMovementTypeResponseDto create(InventoryMovementTypeCreateDto dto) {
        InventoryMovementType type = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(type));
    }

    @Override
    @CacheEvict(cacheNames = "inventoryMovementTypes", allEntries = true)
    public InventoryMovementTypeResponseDto update(Long id, InventoryMovementTypeUpdateDto dto) {
        InventoryMovementType type = repository.findById(id)
                .orElseThrow(() -> new AppException("Тип движения запасов не найден, id: " + id));
        type.setName(dto.getName());
        type.setCode(dto.getCode());
        return mapper.toResponse(repository.save(type));
    }

    @Override
    @CacheEvict(cacheNames = "inventoryMovementTypes", allEntries = true)
    public void delete(Long id) {
        InventoryMovementType type = repository.findById(id)
                .orElseThrow(() -> new AppException("Тип движения запасов не найден, id: " + id));
        repository.delete(type);
    }

    @Override
    @Cacheable(cacheNames = "inventoryMovementTypes", key = "'inventoryMovementTypes-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.sortDirection + '-' + #request.code + '-' + #request.name")
    public List<InventoryMovementTypeResponseDto> getMovementsWithFilters(InventoryMovementTypeRequestDto request) {
        List<InventoryMovementType> types = repository.findAll();

        types = types.stream()
                .filter(r -> matchesFilter(r.getCode(), request.getCode()))
                .filter(r -> matchesFilter(r.getName(), request.getName()))
                .collect(Collectors.toList());

        types.sort(getSortComparator(request.getSortBy(), request.getSortDirection()));

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), types.size());
        if (fromIndex >= types.size()) {
            return List.of();
        }

        return types.subList(fromIndex, toIndex).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(String fieldValue, String filter) {
        return filter == null || (fieldValue != null && fieldValue.toLowerCase().contains(filter.toLowerCase()));
    }

    private Comparator<InventoryMovementType> getSortComparator(String sortBy, String sortDirection) {
        Comparator<InventoryMovementType> comparator = switch (sortBy != null ? sortBy : "") {
            case "code" -> Comparator.comparing(r -> safeString(r.getCode()));
            case "name" -> Comparator.comparing(r -> safeString(r.getName()));
            default -> Comparator.comparing(InventoryMovementType::getId);
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private String safeString(String value) {
        return value != null ? value.toLowerCase() : "";
    }
}
