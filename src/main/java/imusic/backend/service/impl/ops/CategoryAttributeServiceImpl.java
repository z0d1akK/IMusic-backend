package imusic.backend.service.impl.ops;

import imusic.backend.dto.create.ops.CategoryAttributeCreateDto;
import imusic.backend.dto.update.ops.CategoryAttributeUpdateDto;
import imusic.backend.dto.response.ops.CategoryAttributeResponseDto;
import imusic.backend.entity.ops.CategoryAttribute;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.CategoryAttributeMapper;
import imusic.backend.mapper.resolver.ref.ProductCategoryResolver;
import imusic.backend.repository.ops.CategoryAttributeRepository;
import imusic.backend.service.ops.CategoryAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryAttributeServiceImpl implements CategoryAttributeService {

    private final CategoryAttributeRepository categoryAttributeRepository;
    private final CategoryAttributeMapper categoryAttributeMapper;
    private final ProductCategoryResolver productCategoryResolver;

    @Override
    @Cacheable(cacheNames = "categoryAttributes", key = "'all'")
    public List<CategoryAttributeResponseDto> getAll() {
        return categoryAttributeRepository.findAll().stream()
                .map(categoryAttributeMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(cacheNames = "categoryAttributes", key = "#id")
    public CategoryAttributeResponseDto getById(Long id) {
        CategoryAttribute attribute = categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new AppException("Атрибут категории не найден, ID: " + id));
        return categoryAttributeMapper.toResponse(attribute);
    }

    @Override
    @Cacheable(cacheNames = "categoryAttributes", key = "#categoryId")
    public List<CategoryAttributeResponseDto> getByCategoryId(Long categoryId) {
        return categoryAttributeRepository.findByCategoryId(categoryId)
                .stream()
                .map(categoryAttributeMapper::toResponse)
                .toList();
    }

    @Override
    @CachePut(cacheNames = "categoryAttributes", key = "#result.id")
    @CacheEvict(cacheNames = "categoryAttributes", allEntries = true)
    public CategoryAttributeResponseDto create(CategoryAttributeCreateDto dto) {
        CategoryAttribute attribute = categoryAttributeMapper.toEntity(dto, productCategoryResolver);
        return categoryAttributeMapper.toResponse(categoryAttributeRepository.save(attribute));
    }

    @Override
    @CacheEvict(cacheNames = "categoryAttributes", allEntries = true)
    public CategoryAttributeResponseDto update(Long id, CategoryAttributeUpdateDto dto) {
        CategoryAttribute attribute = categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new AppException("Атрибут категории не найден, ID: " + id));
        categoryAttributeMapper.updateEntity(dto, attribute, productCategoryResolver);
        return categoryAttributeMapper.toResponse(categoryAttributeRepository.save(attribute));
    }

    @Override
    @CacheEvict(cacheNames = "categoryAttributes", allEntries = true)
    public void delete(Long id) {
        CategoryAttribute attribute = categoryAttributeRepository.findById(id)
                .orElseThrow(() -> new AppException("Атрибут категории не найден, ID: " + id));
        categoryAttributeRepository.delete(attribute);
    }
}
