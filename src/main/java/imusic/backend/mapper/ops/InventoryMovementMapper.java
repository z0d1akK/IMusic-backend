package imusic.backend.mapper.ops;

import imusic.backend.dto.create.ops.InventoryMovementCreateDto;
import imusic.backend.dto.update.ops.InventoryMovementUpdateDto;
import imusic.backend.dto.response.ops.InventoryMovementResponseDto;
import imusic.backend.entity.ops.InventoryMovement;
import imusic.backend.mapper.resolver.ops.ProductResolver;
import imusic.backend.mapper.resolver.ops.UserResolver;
import imusic.backend.mapper.resolver.ref.InventoryMovementTypeResolver;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface InventoryMovementMapper {

    @Mapping(source = "product.id",              target = "productId")
    @Mapping(source = "product.name",            target = "productName")
    @Mapping(source = "movementType.id",         target = "movementTypeId")
    @Mapping(source = "movementType.code",       target = "movementTypeCode")
    @Mapping(source = "movementType.name",       target = "movementTypeName")
    @Mapping(source = "createdBy.id",            target = "createdById")
    @Mapping(source = "createdBy.username",      target = "createdByName")
    InventoryMovementResponseDto toResponse(InventoryMovement entity);

    @Mapping(target = "product",       expression = "java(productResolver.resolve(dto.getProductId()))")
    @Mapping(target = "movementType",  expression = "java(movementTypeResolver.resolve(dto.getMovementTypeId()))")
    @Mapping(target = "createdBy",     expression = "java(userResolver.resolve(dto.getCreatedById()))")
    InventoryMovement toEntity(InventoryMovementCreateDto dto,
                               @Context ProductResolver productResolver,
                               @Context InventoryMovementTypeResolver movementTypeResolver,
                               @Context UserResolver userResolver);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "movementType",
            expression = "java(dto.getMovementTypeId() != null ? movementTypeResolver.resolve(dto.getMovementTypeId()) : entity.getMovementType())")
    void updateEntity(InventoryMovementUpdateDto dto,
                             @MappingTarget InventoryMovement entity,
                             @Context ProductResolver productResolver,
                             @Context InventoryMovementTypeResolver movementTypeResolver);
}

