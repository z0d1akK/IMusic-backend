package imusic.backend.mapper.ref;

import imusic.backend.dto.create.ref.OrderStatusCreateDto;
import imusic.backend.dto.update.ref.OrderStatusUpdateDto;
import imusic.backend.dto.response.ref.OrderStatusResponseDto;
import imusic.backend.entity.ref.OrderStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface OrderStatusMapper {

    OrderStatusResponseDto toResponse(OrderStatus entity);

    OrderStatus toEntity(OrderStatusCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(OrderStatusUpdateDto dto, @MappingTarget OrderStatus entity);
}
