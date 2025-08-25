package imusic.backend.service.impl.ref;

import imusic.backend.dto.request.ref.ProductUnitRequestDto;
import imusic.backend.dto.response.ref.ProductUnitResponseDto;
import imusic.backend.dto.create.ref.ProductUnitCreateDto;
import imusic.backend.dto.update.ref.ProductUnitUpdateDto;
import imusic.backend.entity.ref.ProductUnit;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.ProductUnitMapper;
import imusic.backend.repository.ref.ProductUnitRepository;
import imusic.backend.service.ref.ProductUnitService;
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
public class ProductUnitServiceImpl implements ProductUnitService {

    private final ProductUnitRepository repository;
    private final ProductUnitMapper mapper;

    @Override
    @Cacheable(cacheNames = "productUnits", key = "'all'")
    public List<ProductUnitResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "productUnits", key = "#id")
    public ProductUnitResponseDto getById(Long id) {
        ProductUnit unit = repository.findById(id)
                .orElseThrow(() -> new AppException("Единица продукта не найдена, id: " + id));
        return mapper.toResponse(unit);
    }

    @Override
    @CacheEvict(cacheNames = "productUnits", allEntries = true)
    @CachePut(cacheNames = "productUnits", key = "#result.id")
    public ProductUnitResponseDto create(ProductUnitCreateDto dto) {
        ProductUnit unit = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(unit));
    }

    @Override
    @CacheEvict(cacheNames = "productUnits", allEntries = true)
    public ProductUnitResponseDto update(Long id, ProductUnitUpdateDto dto) {
        ProductUnit unit = repository.findById(id)
                .orElseThrow(() -> new AppException("Единица продукта не найдена, id: " + id));
        unit.setName(dto.getName());
        unit.setCode(dto.getCode());
        return mapper.toResponse(repository.save(unit));
    }

    @Override
    @CacheEvict(cacheNames = "productUnits", allEntries = true)
    public void delete(Long id) {
        ProductUnit unit = repository.findById(id)
                .orElseThrow(() -> new AppException("Единица продукта не найдена, id: " + id));
        repository.delete(unit);
    }

    @Override
    @Cacheable(cacheNames = "productUnits", key = "'productUnits-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.sortDirection + '-' + #request.code + '-' + #request.name")
    public List<ProductUnitResponseDto> getUnitsWithFilters(ProductUnitRequestDto request) {
        List<ProductUnit> units = repository.findAll();

        units = units.stream()
                .filter(r -> matchesFilter(r.getCode(), request.getCode()))
                .filter(r -> matchesFilter(r.getName(), request.getName()))
                .collect(Collectors.toList());

        units.sort(getSortComparator(request.getSortBy(), request.getSortDirection()));

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), units.size());
        if (fromIndex >= units.size()) {
            return List.of();
        }

        return units.subList(fromIndex, toIndex).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(String fieldValue, String filter) {
        return filter == null || (fieldValue != null && fieldValue.toLowerCase().contains(filter.toLowerCase()));
    }

    private Comparator<ProductUnit> getSortComparator(String sortBy, String sortDirection) {
        Comparator<ProductUnit> comparator = switch (sortBy != null ? sortBy : "") {
            case "code" -> Comparator.comparing(r -> safeString(r.getCode()));
            case "name" -> Comparator.comparing(r -> safeString(r.getName()));
            default -> Comparator.comparing(ProductUnit::getId);
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
