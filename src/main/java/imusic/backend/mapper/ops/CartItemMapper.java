package imusic.backend.mapper.ops;

import imusic.backend.dto.create.ops.CartItemCreateDto;
import imusic.backend.dto.update.ops.CartItemUpdateDto;
import imusic.backend.dto.response.ops.CartItemResponseDto;
import imusic.backend.entity.ops.CartItem;
import imusic.backend.mapper.resolver.ops.CartResolver;
import imusic.backend.mapper.resolver.ops.ProductResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    @Mapping(source = "cart.id", target = "cartId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "product.price", target = "productPrice")
    @Mapping(source = "product.stockQuantity", target = "productStockQuantity")
    @Mapping(source = "product.imagePath", target = "productImagePath")
    CartItemResponseDto toResponse(CartItem entity);

    @Mapping(target = "cart", expression = "java(cartResolver.resolve(dto.getCartId()))")
    @Mapping(target = "product", expression = "java(productResolver.resolve(dto.getProductId()))")
    CartItem toEntity(CartItemCreateDto dto,
                      @Context CartResolver cartResolver,
                      @Context ProductResolver productResolver);

    @Mapping(target = "product", expression = "java(productResolver.resolve(dto.getProductId()))")
    @Mapping(target = "quantity", source = "quantity")
    void updateEntity(CartItemUpdateDto dto,
                      @MappingTarget CartItem entity,
                      @Context CartResolver cartResolver,
                      @Context ProductResolver productResolver);
}
