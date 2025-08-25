package imusic.backend.service.impl.ref;

import imusic.backend.dto.request.ref.ProductCategoryRequestDto;
import imusic.backend.dto.response.ref.ProductCategoryResponseDto;
import imusic.backend.dto.create.ref.ProductCategoryCreateDto;
import imusic.backend.dto.update.ref.ProductCategoryUpdateDto;
import imusic.backend.entity.ref.ProductCategory;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.ProductCategoryMapper;
import imusic.backend.repository.ref.ProductCategoryRepository;
import imusic.backend.service.ref.ProductCategoryService;
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
public class ProductCategoryServiceImpl implements ProductCategoryService {

    private final ProductCategoryRepository repository;
    private final ProductCategoryMapper mapper;

    @Override
    @Cacheable(cacheNames = "productCategories", key = "'all'")
    public List<ProductCategoryResponseDto> getAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "productCategories", key = "#id")
    public ProductCategoryResponseDto getById(Long id) {
        ProductCategory category = repository.findById(id)
                .orElseThrow(() -> new AppException("Категория продукта не найдена, id: " + id));
        return mapper.toResponse(category);
    }

    @Override
    @CacheEvict(cacheNames = "productCategories", allEntries = true)
    @CachePut(cacheNames = "productCategories", key = "#result.id")
    public ProductCategoryResponseDto create(ProductCategoryCreateDto dto) {
        ProductCategory category = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(category));
    }

    @Override
    @CacheEvict(cacheNames = "productCategories", allEntries = true)
    public ProductCategoryResponseDto update(Long id, ProductCategoryUpdateDto dto) {
        ProductCategory category = repository.findById(id)
                .orElseThrow(() -> new AppException("Категория продукта не найдена, id: " + id));
        category.setName(dto.getName());
        category.setCode(dto.getCode());
        return mapper.toResponse(repository.save(category));
    }

    @Override
    @CacheEvict(cacheNames = "productCategories", allEntries = true)
    public void delete(Long id) {
        ProductCategory category = repository.findById(id)
                .orElseThrow(() -> new AppException("Категория продукта не найдена, id: " + id));
        repository.delete(category);
    }

    @Override
    @Cacheable(cacheNames = "productCategories", key = "'productCategories-' + #request.page + '-' + #request.size + '-' + #request.sortBy + '-' + #request.sortDirection + '-' + #request.code + '-' + #request.name")
    public List<ProductCategoryResponseDto> getCategoriesWithFilters(ProductCategoryRequestDto request) {
        List<ProductCategory> categories = repository.findAll();

        categories = categories.stream()
                .filter(r -> matchesFilter(r.getCode(), request.getCode()))
                .filter(r -> matchesFilter(r.getName(), request.getName()))
                .collect(Collectors.toList());

        categories.sort(getSortComparator(request.getSortBy(), request.getSortDirection()));

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), categories.size());
        if (fromIndex >= categories.size()) {
            return List.of();
        }

        return categories.subList(fromIndex, toIndex).stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    private boolean matchesFilter(String fieldValue, String filter) {
        return filter == null || (fieldValue != null && fieldValue.toLowerCase().contains(filter.toLowerCase()));
    }

    private Comparator<ProductCategory> getSortComparator(String sortBy, String sortDirection) {
        Comparator<ProductCategory> comparator = switch (sortBy != null ? sortBy : "") {
            case "code" -> Comparator.comparing(r -> safeString(r.getCode()));
            case "name" -> Comparator.comparing(r -> safeString(r.getName()));
            default -> Comparator.comparing(ProductCategory::getId);
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
