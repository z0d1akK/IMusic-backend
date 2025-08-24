package imusic.backend.mapper.ops;

import imusic.backend.dto.create.ops.NotificationCreateDto;
import imusic.backend.dto.update.ops.NotificationUpdateDto;
import imusic.backend.dto.response.ops.NotificationResponseDto;
import imusic.backend.entity.ops.Notification;
import imusic.backend.mapper.resolver.ops.UserResolver;
import imusic.backend.mapper.resolver.ref.NotificationTypeResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "user.id",        target = "userId")
    @Mapping(source = "type.id",        target = "typeId")
    @Mapping(source = "type.code",      target = "typeCode")
    @Mapping(source = "type.name",     target = "typeName")
    NotificationResponseDto toResponse(Notification entity);

    @Mapping(target = "user", expression = "java(userResolver.resolve(dto.getUserId()))")
    @Mapping(target = "type", expression = "java(notificationTypeResolver.resolve(dto.getTypeId()))")
    Notification toEntity(NotificationCreateDto dto,
                          @Context UserResolver userResolver,
                          @Context NotificationTypeResolver notificationTypeResolver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(NotificationUpdateDto dto,
                             @MappingTarget Notification entity);
}

