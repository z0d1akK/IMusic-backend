package imusic.backend.service.ref;

import imusic.backend.dto.request.ref.ProductCategoryRequestDto;
import imusic.backend.dto.response.ref.ProductCategoryResponseDto;
import imusic.backend.dto.create.ref.ProductCategoryCreateDto;
import imusic.backend.dto.update.ref.ProductCategoryUpdateDto;

import java.util.List;

public interface ProductCategoryService {
    List<ProductCategoryResponseDto> getAll();
    ProductCategoryResponseDto getById(Long id);
    ProductCategoryResponseDto create(ProductCategoryCreateDto dto);
    ProductCategoryResponseDto update(Long id, ProductCategoryUpdateDto dto);
    void delete(Long id);

    List<ProductCategoryResponseDto> getCategoriesWithFilters(ProductCategoryRequestDto request);
}
