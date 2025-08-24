package imusic.backend.mapper.ref;

import imusic.backend.dto.create.ref.NotificationTypeCreateDto;
import imusic.backend.dto.update.ref.NotificationTypeUpdateDto;
import imusic.backend.dto.response.ref.NotificationTypeResponseDto;
import imusic.backend.entity.ref.NotificationType;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationTypeMapper {

    NotificationTypeResponseDto toResponse(NotificationType entity);

    NotificationType toEntity(NotificationTypeCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(NotificationTypeUpdateDto dto, @MappingTarget NotificationType entity);
}
