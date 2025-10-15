package imusic.backend.mapper.ops;

import imusic.backend.mapper.resolver.ops.UserResolver;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import imusic.backend.dto.create.ops.OrderCreateDto;
import imusic.backend.dto.update.ops.OrderUpdateDto;
import imusic.backend.dto.response.ops.OrderResponseDto;
import imusic.backend.entity.ops.Order;
import imusic.backend.mapper.resolver.ops.ClientResolver;
import imusic.backend.mapper.resolver.ref.OrderStatusResolver;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.companyName", target = "clientName")
    @Mapping(source = "createdBy.id", target = "createdById")
    @Mapping(source = "createdBy.username", target = "createdByName")
    @Mapping(source = "status.id", target = "statusId")
    @Mapping(source = "status.code", target = "statusCode")
    @Mapping(source = "status.name", target = "statusName")
    OrderResponseDto toResponse(Order entity);

    @Mapping(target = "client", expression = "java(clientResolver.resolve(dto.getClientId()))")
    @Mapping(target = "createdBy", expression = "java(userResolver.resolve(dto.getCreatedBy()))")
    @Mapping(target = "status", expression = "java(orderStatusResolver.resolve(dto.getStatusId()))")
    Order toEntity(OrderCreateDto dto,
                   @Context ClientResolver clientResolver,
                   @Context UserResolver userResolver,
                   @Context OrderStatusResolver orderStatusResolver);

    @Mapping(target = "status", expression = "java(orderStatusResolver.resolve(dto.getStatusId()))")
    void updateEntity(OrderUpdateDto dto,
                      @Context OrderStatusResolver orderStatusResolver,
                      @MappingTarget Order entity);
}


