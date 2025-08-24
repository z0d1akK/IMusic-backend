package imusic.backend.mapper.ref;

import org.mapstruct.*;
import imusic.backend.entity.ref.PaymentMethod;
import imusic.backend.dto.create.ref.PaymentMethodCreateDto;
import imusic.backend.dto.update.ref.PaymentMethodUpdateDto;
import imusic.backend.dto.response.ref.PaymentMethodResponseDto;

@Mapper(componentModel = "spring")
public interface PaymentMethodMapper {

    PaymentMethodResponseDto toResponse(PaymentMethod entity);

    PaymentMethod toEntity(PaymentMethodCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(PaymentMethodUpdateDto dto, @MappingTarget PaymentMethod entity);
}
