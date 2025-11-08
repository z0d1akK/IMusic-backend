package imusic.backend.service.impl.ops;

import imusic.backend.dto.create.ops.ProductAttributeCreateDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.update.ops.ProductAttributeUpdateDto;
import imusic.backend.dto.request.ops.ProductAttributeRequestDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;
import imusic.backend.entity.ops.ProductAttribute;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.ProductAttributeMapper;
import imusic.backend.mapper.resolver.ops.CategoryAttributeResolver;
import imusic.backend.mapper.resolver.ops.ProductResolver;
import imusic.backend.repository.ops.ProductAttributeRepository;
import imusic.backend.service.ops.ProductAttributeService;
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
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository attributeRepository;
    private final ProductAttributeMapper attributeMapper;
    private final CategoryAttributeResolver categoryAttributeResolver;
    private final ProductResolver productResolver;

    @Override
    @Cacheable(cacheNames = "productAttributes", key = "'all'")
    public List<ProductAttributeResponseDto> getAll() {
        return attributeRepository.findAll().stream()
                .map(attributeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "productAttributes", key = "#id")
    public ProductAttributeResponseDto getById(Long id) {
        ProductAttribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new AppException("Атрибут товара не найден, ID: " + id));
        return attributeMapper.toResponse(attribute);
    }

    @Override
    @Cacheable(cacheNames = "productAttributes", key = "#productId")
    public List<ProductAttributeResponseDto> getAllByProductId(Long productId) {
        return attributeRepository.findAllByProduct_Id(productId)
                .stream()
                .map(attributeMapper::toResponse)
                .toList();
    }

    @Override
    @CachePut(cacheNames = "productAttributes", key = "#result.id")
    @CacheEvict(cacheNames = "productAttributes", allEntries = true)
    public ProductAttributeResponseDto create(ProductAttributeCreateDto dto) {
        ProductAttribute attribute = attributeMapper.toEntity(dto, categoryAttributeResolver, productResolver);
        return attributeMapper.toResponse(attributeRepository.save(attribute));
    }

    @Override
    @CacheEvict(cacheNames = "productAttributes", allEntries = true)
    public ProductAttributeResponseDto update(Long id, ProductAttributeUpdateDto dto) {
        ProductAttribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new AppException("Атрибут товара не найден, ID: " + id));
        attributeMapper.updateEntity(dto, attribute, categoryAttributeResolver, productResolver);
        return attributeMapper.toResponse(attributeRepository.save(attribute));
    }

    @Override
    @CacheEvict(cacheNames = "productAttributes", allEntries = true)
    public void delete(Long id) {
        ProductAttribute attribute = attributeRepository.findById(id)
                .orElseThrow(() -> new AppException("Атрибут товара не найден, ID: " + id));
        attributeRepository.delete(attribute);
    }

    @Override
    public PageResponseDto<ProductAttributeResponseDto> getPagedAttributes(ProductAttributeRequestDto request) {
        List<ProductAttribute> attributes = attributeRepository.findAll();

        if (request.getProductId() != null) {
            attributes = attributes.stream()
                    .filter(a -> a.getProduct() != null && a.getProduct().getId().equals(request.getProductId()))
                    .collect(Collectors.toList());
        }
        if (request.getCategoryAttributeId() != null) {
            attributes = attributes.stream()
                    .filter(a -> a.getCategoryAttribute() != null &&
                            a.getCategoryAttribute().getId().equals(request.getCategoryAttributeId()))
                    .collect(Collectors.toList());
        }
        if (request.getValue() != null && !request.getValue().isBlank()) {
            attributes = attributes.stream()
                    .filter(a -> a.getValue() != null &&
                            a.getValue().toLowerCase().contains(request.getValue().toLowerCase()))
                    .collect(Collectors.toList());
        }

        attributes.sort(getAttributeSortComparator(request.getSortBy(), request.getSortDirection()));

        int totalElements = attributes.size();
        int totalPages = (int) Math.ceil((double) totalElements / request.getSize());

        int fromIndex = Math.max(0, request.getPage() * request.getSize());
        int toIndex = Math.min(fromIndex + request.getSize(), totalElements);

        List<ProductAttributeResponseDto> content = attributes.subList(fromIndex, toIndex)
                .stream()
                .map(attributeMapper::toResponse)
                .toList();

        return new PageResponseDto<>(content, request.getPage(), request.getSize(), totalElements, totalPages);
    }

    private Comparator<ProductAttribute> getAttributeSortComparator(String sortBy, String sortDirection) {
        if (sortBy == null || sortBy.isBlank()) {
            sortBy = "id";
        }

        Comparator<ProductAttribute> comparator = switch (sortBy) {
            case "value" -> Comparator.comparing(a -> safeString(a.getValue()), String.CASE_INSENSITIVE_ORDER);
            case "product" -> Comparator.comparing(
                    a -> a.getProduct() != null ? safeString(a.getProduct().getName()) : "",
                    String.CASE_INSENSITIVE_ORDER);
            case "categoryAttribute" -> Comparator.comparing(
                    a -> a.getCategoryAttribute() != null ? safeString(a.getCategoryAttribute().getName()) : "",
                    String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(ProductAttribute::getId);
        };

        if ("desc".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return comparator;
    }

    private String safeString(String value) {
        return value != null ? value.toLowerCase() : "";
    }

    @Override
    public List<ProductAttributeResponseDto> getByProductId(Long productId) {
        return getAllByProductId(productId).stream()
                .map(attr -> {
                    if (attr.getValue() == null || attr.getValue().isBlank()) {
                        attr.setValue(attr.getDefaultValue());
                    }
                    return attr;
                })
                .toList();
    }

    @Override
    public ProductAttributeResponseDto updateOrCreate(ProductAttributeUpdateDto dto) {
        return attributeRepository.findByProduct_IdAndCategoryAttribute_Id(dto.getProductId(), dto.getCategoryAttributeId())
                .map(attr -> {
                    attr.setValue(dto.getValue());
                    return attributeMapper.toResponse(attributeRepository.save(attr));
                })
                .orElseGet(() -> {
                    ProductAttributeCreateDto createDto = new ProductAttributeCreateDto();
                    createDto.setProductId(dto.getProductId());
                    createDto.setCategoryAttributeId(dto.getCategoryAttributeId());
                    createDto.setValue(dto.getValue());
                    return create(createDto);
                });
    }


}
