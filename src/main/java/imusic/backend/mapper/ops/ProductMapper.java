package imusic.backend.mapper.ops;

import imusic.backend.dto.create.ops.ProductCreateDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.dto.update.ops.ProductUpdateDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;
import imusic.backend.entity.ops.Product;
import imusic.backend.service.ops.ProductAttributeService;
import imusic.backend.mapper.resolver.ref.ProductCategoryResolver;
import imusic.backend.mapper.resolver.ref.ProductUnitResolver;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring", uses = {ProductUnitResolver.class, ProductCategoryResolver.class})
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "unit.id", target = "unitId")
    @Mapping(source = "unit.name", target = "unitName")
    @Mapping(target = "attributes", expression = "java(getProductAttributes(entity))")
    ProductResponseDto toResponse(Product entity);

    default List<ProductAttributeResponseDto> getProductAttributes(Product product) {
        if (product == null || product.getId() == null) return List.of();
        return ProductAttributeServiceStaticHolder.productAttributeService.getAllByProductId(product.getId());
    }

    @Mapping(target = "category", expression = "java(categoryResolver.resolve(dto.getCategoryId()))")
    @Mapping(target = "unit", expression = "java(unitResolver.resolve(dto.getUnitId()))")
    Product toEntity(ProductCreateDto dto,
                     @Context ProductCategoryResolver categoryResolver,
                     @Context ProductUnitResolver unitResolver);

    @Mapping(target = "category", expression = "java(dto.getCategoryId() != null ? categoryResolver.resolve(dto.getCategoryId()) : entity.getCategory())")
    @Mapping(target = "unit", expression = "java(dto.getUnitId() != null ? unitResolver.resolve(dto.getUnitId()) : entity.getUnit())")
    void updateEntity(ProductUpdateDto dto,
                      @Context ProductCategoryResolver categoryResolver,
                      @Context ProductUnitResolver unitResolver,
                      @MappingTarget Product entity);

    class ProductAttributeServiceStaticHolder {
        public static ProductAttributeService productAttributeService;
    }
}
