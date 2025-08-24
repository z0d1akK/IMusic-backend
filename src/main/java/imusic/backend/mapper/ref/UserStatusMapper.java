package imusic.backend.mapper.ref;

import imusic.backend.dto.create.ref.UserStatusCreateDto;
import imusic.backend.dto.update.ref.UserStatusUpdateDto;
import imusic.backend.dto.response.ref.UserStatusResponseDto;
import imusic.backend.entity.ref.UserStatus;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserStatusMapper {

    UserStatusResponseDto toResponse(UserStatus entity);

    UserStatus toEntity(UserStatusCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(UserStatusUpdateDto dto, @MappingTarget UserStatus entity);
}
