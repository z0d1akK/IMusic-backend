package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.ProductCreateDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.CategoryAttributeResponseDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;
import imusic.backend.dto.update.ops.ProductUpdateDto;
import imusic.backend.dto.request.ops.ProductRequestDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    List<ProductResponseDto> getAll();
    ProductResponseDto getById(Long id);
    ProductResponseDto create(ProductCreateDto dto);
    ProductResponseDto update(Long id, ProductUpdateDto dto);
    void delete(Long id);
    void uploadProductImage(Long id, MultipartFile file);
    List<ProductAttributeResponseDto> getAttributesByProductId(Long productId);
    List<CategoryAttributeResponseDto> getCategoryAttributesWithValues(Long productId);
    PageResponseDto<ProductResponseDto> getPagedProducts(ProductRequestDto request);
}
