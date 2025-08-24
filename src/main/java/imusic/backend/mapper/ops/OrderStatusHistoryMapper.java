package imusic.backend.mapper.ops;

import imusic.backend.dto.create.ops.OrderStatusHistoryCreateDto;
import imusic.backend.dto.response.ops.OrderStatusHistoryResponseDto;
import imusic.backend.entity.ops.OrderStatusHistory;
import imusic.backend.mapper.resolver.ops.OrderResolver;
import imusic.backend.mapper.resolver.ops.UserResolver;
import imusic.backend.mapper.resolver.ref.OrderStatusResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderStatusHistoryMapper {
    @Mapping(source = "order.id", target = "orderId")
    @Mapping(source = "oldStatus.id", target = "oldStatusId")
    @Mapping(source = "oldStatus.code", target = "oldStatusCode")
    @Mapping(source = "oldStatus.name", target = "oldStatusName")
    @Mapping(source = "newStatus.id", target = "newStatusId")
    @Mapping(source = "newStatus.code", target = "newStatusCode")
    @Mapping(source = "newStatus.name", target = "newStatusName")
    @Mapping(source = "changedBy.id", target = "changedById")
    @Mapping(source = "changedBy.username", target = "changedByName")
    OrderStatusHistoryResponseDto toResponse(OrderStatusHistory entity);

    @Mapping(target = "order", expression = "java(orderResolver.resolve(dto.getOrderId()))")
    @Mapping(target = "oldStatus", expression = "java(orderStatusResolver.resolve(dto.getOldStatusId()))")
    @Mapping(target = "newStatus", expression = "java(orderStatusResolver.resolve(dto.getNewStatusId()))")
    @Mapping(target = "changedBy", expression = "java(userResolver.resolve(dto.getChangedById()))")
    OrderStatusHistory toEntity(OrderStatusHistoryCreateDto dto,
                                @Context OrderResolver orderResolver,
                                @Context OrderStatusResolver orderStatusResolver,
                                @Context UserResolver userResolver);
}

