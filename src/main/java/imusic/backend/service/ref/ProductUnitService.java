package imusic.backend.service.ref;

import imusic.backend.dto.request.ref.ProductUnitRequestDto;
import imusic.backend.dto.response.ref.ProductUnitResponseDto;
import imusic.backend.dto.create.ref.ProductUnitCreateDto;
import imusic.backend.dto.update.ref.ProductUnitUpdateDto;

import java.util.List;

public interface ProductUnitService {
    List<ProductUnitResponseDto> getAll();
    ProductUnitResponseDto getById(Long id);
    ProductUnitResponseDto create(ProductUnitCreateDto dto);
    ProductUnitResponseDto update(Long id, ProductUnitUpdateDto dto);
    void delete(Long id);
    List<ProductUnitResponseDto> getUnitsWithFilters(ProductUnitRequestDto request);
}
