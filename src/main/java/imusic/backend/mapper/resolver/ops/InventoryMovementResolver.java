package imusic.backend.mapper.resolver.ops;

import org.springframework.stereotype.Component;
import imusic.backend.entity.ops.InventoryMovement;
import imusic.backend.mapper.resolver.base.BaseEntityResolver;
import imusic.backend.repository.ops.InventoryMovementRepository;

@Component
public class InventoryMovementResolver extends BaseEntityResolver<InventoryMovement, Long> {

    public InventoryMovementResolver(InventoryMovementRepository inventoryMovementRepository) {
        super(inventoryMovementRepository);
    }

    @Override
    protected String getEntityClassName() {
        return "InventoryMovement";
    }
}
