package imusic.backend.mapper.ops;

import imusic.backend.dto.create.ops.CategoryAttributeCreateDto;
import imusic.backend.dto.update.ops.CategoryAttributeUpdateDto;
import imusic.backend.dto.response.ops.CategoryAttributeResponseDto;
import imusic.backend.entity.ops.CategoryAttribute;
import imusic.backend.mapper.resolver.ref.ProductCategoryResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CategoryAttributeMapper {

    @Mapping(source = "category.id",   target = "categoryId")
    @Mapping(source = "category.code", target = "categoryCode")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "defaultValue", target = "defaultValue")
    CategoryAttributeResponseDto toResponse(CategoryAttribute entity);

    @Mapping(target = "category", expression = "java(productCategoryResolver.resolve(dto.getCategoryId()))")
    @Mapping(target = "defaultValue", source = "dto.defaultValue")
    CategoryAttribute toEntity(CategoryAttributeCreateDto dto,
                               @Context ProductCategoryResolver productCategoryResolver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "category",
            expression = "java(dto.getCategoryId() != null ? productCategoryResolver.resolve(dto.getCategoryId()) : entity.getCategory())")
    void updateEntity(CategoryAttributeUpdateDto dto,
                             @MappingTarget CategoryAttribute entity,
                             @Context ProductCategoryResolver productCategoryResolver);
}