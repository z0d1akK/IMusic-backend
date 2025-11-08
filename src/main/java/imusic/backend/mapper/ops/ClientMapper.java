package imusic.backend.mapper.ops;

import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import imusic.backend.dto.create.ops.ClientCreateDto;
import imusic.backend.dto.update.ops.ClientUpdateDto;
import imusic.backend.dto.response.ops.ClientResponseDto;
import imusic.backend.entity.ops.Client;
import imusic.backend.mapper.resolver.ops.UserResolver;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.username", target = "username")
    @Mapping(source = "createdBy.username", target = "createdBy")
    @Mapping(source = "user.createdAt", target = "userCreatedAt")
    @Mapping(source = "user.status.code", target = "statusCode")
    ClientResponseDto toResponse(Client entity);

    @Mapping(target = "user", expression = "java(userResolver.resolve(dto.getUserId()))")
//    @Mapping(target = "createdBy", ignore = true)
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    Client toEntity(ClientCreateDto dto,
                    @Context UserResolver userResolver);

//    @Mapping(target = "createdBy", ignore = true)
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(ClientUpdateDto dto,
                      @Context UserResolver userResolver,
                      @MappingTarget Client entity);
}
