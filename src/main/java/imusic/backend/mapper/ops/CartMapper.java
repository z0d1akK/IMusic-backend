package imusic.backend.mapper.ops;

import org.mapstruct.*;
import imusic.backend.dto.create.ops.CartCreateDto;
import imusic.backend.dto.update.ops.CartUpdateDto;
import imusic.backend.dto.response.ops.CartResponseDto;
import imusic.backend.entity.ops.Cart;
import imusic.backend.mapper.resolver.ops.ClientResolver;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {

    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "client.companyName", target = "clientName")
    @Mapping(source = "items", target = "items")
    CartResponseDto toResponse(Cart entity);

    @Mapping(target = "client", expression = "java(clientResolver.resolve(dto.getClientId()))")
    @Mapping(target = "items", ignore = true)
    Cart toEntity(CartCreateDto dto,
                  @Context ClientResolver clientResolver);

    @Mapping(target = "client", expression = "java(dto.getClientId() != null ? clientResolver.resolve(dto.getClientId()) : entity.getClient())")
    @Mapping(target = "items", ignore = true) // обработка items идёт отдельно (в сервисе)
    void updateEntity(CartUpdateDto dto,
                      @Context ClientResolver clientResolver,
                      @MappingTarget Cart entity);
}
