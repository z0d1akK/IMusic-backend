package imusic.backend.mapper.ref;

import imusic.backend.dto.create.ref.RoleCreateDto;
import imusic.backend.dto.update.ref.RoleUpdateDto;
import imusic.backend.dto.response.ref.RoleResponseDto;
import imusic.backend.entity.ref.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    RoleResponseDto toResponse(Role entity);

    Role toEntity(RoleCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(RoleUpdateDto dto, @MappingTarget Role entity);
}
