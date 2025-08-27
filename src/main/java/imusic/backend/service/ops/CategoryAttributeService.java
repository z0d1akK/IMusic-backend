package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.CategoryAttributeCreateDto;
import imusic.backend.dto.update.ops.CategoryAttributeUpdateDto;
import imusic.backend.dto.request.ops.CategoryAttributeRequestDto;
import imusic.backend.dto.response.ops.CategoryAttributeResponseDto;

import java.util.List;

public interface CategoryAttributeService {
    List<CategoryAttributeResponseDto> getAll();
    CategoryAttributeResponseDto getById(Long id);

    List<CategoryAttributeResponseDto> getByCategoryId(Long categoryId);

    CategoryAttributeResponseDto create(CategoryAttributeCreateDto dto);
    CategoryAttributeResponseDto update(Long id, CategoryAttributeUpdateDto dto);
    void delete(Long id);
}
