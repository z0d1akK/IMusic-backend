package imusic.backend.mapper.ref;

import org.mapstruct.*;
import imusic.backend.entity.ref.PaymentStatus;
import imusic.backend.dto.create.ref.PaymentStatusCreateDto;
import imusic.backend.dto.update.ref.PaymentStatusUpdateDto;
import imusic.backend.dto.response.ref.PaymentStatusResponseDto;

@Mapper(componentModel = "spring")
public interface PaymentStatusMapper {

    PaymentStatusResponseDto toResponse(PaymentStatus entity);

    PaymentStatus toEntity(PaymentStatusCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PaymentStatusUpdateDto dto, @MappingTarget PaymentStatus entity);
}
