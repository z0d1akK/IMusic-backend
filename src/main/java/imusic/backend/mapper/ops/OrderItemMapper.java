package imusic.backend.mapper.ops;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import imusic.backend.dto.create.ops.OrderItemCreateDto;
import imusic.backend.dto.update.ops.OrderItemUpdateDto;
import imusic.backend.dto.response.ops.OrderItemResponseDto;
import imusic.backend.entity.ops.OrderItem;
import imusic.backend.mapper.resolver.ops.OrderResolver;
import imusic.backend.mapper.resolver.ops.ProductResolver;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    OrderItemResponseDto toResponse(OrderItem entity);

    @Mapping(target = "order", expression = "java(orderResolver.resolve(dto.getOrderId()))")
    @Mapping(target = "product", expression = "java(productResolver.resolve(dto.getProductId()))")
    OrderItem toEntity(OrderItemCreateDto dto,
                       @Context OrderResolver orderResolver,
                       @Context ProductResolver productResolver);

    @Mapping(target = "order", expression = "java(orderResolver.resolve(dto.getOrderId()))")
    @Mapping(target = "product", expression = "java(productResolver.resolve(dto.getProductId()))")
    void updateEntity(OrderItemUpdateDto dto,
                      @Context OrderResolver orderResolver,
                      @Context ProductResolver productResolver,
                      @MappingTarget OrderItem entity);
}


