package imusic.backend.repository.ops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ops.InventoryMovement;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long>{}