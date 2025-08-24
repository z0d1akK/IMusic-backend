package imusic.backend.mapper.ops;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import imusic.backend.dto.response.ops.UserResponseDto;
import imusic.backend.dto.create.ops.UserCreateDto;
import imusic.backend.dto.update.ops.UserUpdateDto;
import imusic.backend.entity.ops.User;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(source = "role.name", target = "roleName")
    @Mapping(source = "status.name", target = "statusName")
    UserResponseDto toResponse(User user);

    @Mapping(target = "role", expression = "java(roleResolver.resolve(dto.getRoleId()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    User toEntity(UserCreateDto dto,
                  @Context RoleResolver roleResolver,
                  @Context UserStatusResolver userStatusResolver);

    @Mapping(target = "role", expression = "java(roleResolver.resolve(dto.getRoleId()))")
    @Mapping(target = "status", expression = "java(userStatusResolver.resolve(dto.getStatusId()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    void updateEntity(UserUpdateDto dto,
                      @Context RoleResolver roleResolver,
                      @Context UserStatusResolver userStatusResolver,
                      @MappingTarget User entity);
}
