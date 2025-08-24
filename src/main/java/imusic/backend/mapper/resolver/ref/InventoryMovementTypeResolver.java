package imusic.backend.mapper.resolver.ref;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ref.InventoryMovementType;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ref.InventoryMovementTypeRepository;

@Component
public class InventoryMovementTypeResolver extends BaseEntityResolver<InventoryMovementType, Long> {

    public InventoryMovementTypeResolver(InventoryMovementTypeRepository inventoryMovementTypeRepository) {
        super(inventoryMovementTypeRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "InventoryMovementType";
    }
}
