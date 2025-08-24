package imusic.backend.mapper.ref;

import imusic.backend.dto.create.ref.InventoryMovementTypeCreateDto;
import imusic.backend.dto.update.ref.InventoryMovementTypeUpdateDto;
import imusic.backend.dto.response.ref.InventoryMovementTypeResponseDto;
import imusic.backend.entity.ref.InventoryMovementType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InventoryMovementTypeMapper {

    InventoryMovementTypeResponseDto toResponse(InventoryMovementType entity);

    InventoryMovementType toEntity(InventoryMovementTypeCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(InventoryMovementTypeUpdateDto dto, @MappingTarget InventoryMovementType entity);
}
