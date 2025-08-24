package imusic.backend.mapper.ops;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import imusic.backend.dto.create.ops.ProductCreateDto;
import imusic.backend.dto.update.ops.ProductUpdateDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.entity.ops.Product;
import imusic.backend.mapper.resolver.ref.ProductCategoryResolver;
import imusic.backend.mapper.resolver.ref.ProductUnitResolver;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(source = "category.id",   target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "unit.id",       target = "unitId")
    @Mapping(source = "unit.name",     target = "unitName")
    ProductResponseDto toResponse(Product entity);

    @Mapping(target = "category", expression = "java(categoryResolver.resolve(dto.getCategoryId()))")
    @Mapping(target = "unit",     expression = "java(unitResolver.resolve(dto.getUnitId()))")
    Product toEntity(ProductCreateDto dto,
                     @Context ProductCategoryResolver categoryResolver,
                     @Context ProductUnitResolver unitResolver);

    @Mapping(target = "category", expression = "java(dto.getCategoryId() != null ? categoryResolver.resolve(dto.getCategoryId()) : entity.getCategory())")
    @Mapping(target = "unit",     expression = "java(dto.getUnitId() != null ? unitResolver.resolve(dto.getUnitId()) : entity.getUnit())")
    void updateEntity(ProductUpdateDto dto,
                      @Context ProductCategoryResolver categoryResolver,
                      @Context ProductUnitResolver unitResolver,
                      @MappingTarget Product entity);
}

