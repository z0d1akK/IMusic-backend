package imusic.backend.mapper.ref;

import imusic.backend.dto.create.ref.ProductCategoryCreateDto;
import imusic.backend.dto.update.ref.ProductCategoryUpdateDto;
import imusic.backend.dto.response.ref.ProductCategoryResponseDto;
import imusic.backend.entity.ref.ProductCategory;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductCategoryMapper {

    ProductCategoryResponseDto toResponse(ProductCategory entity);

    ProductCategory toEntity(ProductCategoryCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductCategoryUpdateDto dto, @MappingTarget ProductCategory entity);
}
