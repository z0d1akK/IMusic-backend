package imusic.backend.mapper.ops;

import imusic.backend.dto.create.ops.ProductAttributeCreateDto;
import imusic.backend.dto.update.ops.ProductAttributeUpdateDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;
import imusic.backend.entity.ops.ProductAttribute;
import imusic.backend.mapper.resolver.ops.CategoryAttributeResolver;
import imusic.backend.mapper.resolver.ops.ProductResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface ProductAttributeMapper {

    @Mapping(source = "categoryAttribute.id",   target = "categoryAttributeId")
    @Mapping(source = "categoryAttribute.name", target = "categoryAttributeName")
    @Mapping(source = "product.id",             target = "productId")
    @Mapping(source = "product.name",           target = "productName")
    @Mapping(source = "categoryAttribute.defaultValue", target = "defaultValue")
    ProductAttributeResponseDto toResponse(ProductAttribute entity);

    @Mapping(target = "categoryAttribute", expression = "java(categoryAttributeResolver.resolve(dto.getCategoryAttributeId()))")
    @Mapping(target = "product",           expression = "java(productResolver.resolve(dto.getProductId()))")
    ProductAttribute toEntity(ProductAttributeCreateDto dto,
                              @Context CategoryAttributeResolver categoryAttributeResolver,
                              @Context ProductResolver productResolver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "categoryAttribute",
            expression = "java(dto.getCategoryAttributeId() != null ? categoryAttributeResolver.resolve(dto.getCategoryAttributeId()) : entity.getCategoryAttribute())")
    @Mapping(target = "product",
            expression = "java(dto.getProductId() != null ? productResolver.resolve(dto.getProductId()) : entity.getProduct())")
    void updateEntity(ProductAttributeUpdateDto dto,
                             @MappingTarget ProductAttribute entity,
                             @Context CategoryAttributeResolver categoryAttributeResolver,
                             @Context ProductResolver productResolver);
}

