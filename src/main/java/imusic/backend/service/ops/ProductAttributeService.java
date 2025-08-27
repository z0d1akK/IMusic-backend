package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.ProductAttributeCreateDto;
import imusic.backend.dto.update.ops.ProductAttributeUpdateDto;
import imusic.backend.dto.request.ops.ProductAttributeRequestDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;

import java.util.List;

public interface ProductAttributeService {
    List<ProductAttributeResponseDto> getAll();
    ProductAttributeResponseDto getById(Long id);

    List<ProductAttributeResponseDto> getAllByProductId(Long productId);

    ProductAttributeResponseDto create(ProductAttributeCreateDto dto);
    ProductAttributeResponseDto update(Long id, ProductAttributeUpdateDto dto);
    void delete(Long id);
    List<ProductAttributeResponseDto> getAttributesWithFilters(ProductAttributeRequestDto request);

    List<ProductAttributeResponseDto> getByProductId(Long productId);

    ProductAttributeResponseDto updateOrCreate(ProductAttributeUpdateDto dto);
}