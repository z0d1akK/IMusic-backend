package imusic.backend.mapper.ref;

import org.mapstruct.*;
import imusic.backend.entity.ref.ProductUnit;
import imusic.backend.dto.create.ref.ProductUnitCreateDto;
import imusic.backend.dto.update.ref.ProductUnitUpdateDto;
import imusic.backend.dto.response.ref.ProductUnitResponseDto;

@Mapper(componentModel = "spring")
public interface ProductUnitMapper {

    ProductUnitResponseDto toResponse(ProductUnit entity);

    ProductUnit toEntity(ProductUnitCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ProductUnitUpdateDto dto, @MappingTarget ProductUnit entity);
}
