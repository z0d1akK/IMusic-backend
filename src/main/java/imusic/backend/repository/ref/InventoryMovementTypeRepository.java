package imusic.backend.repository.ref;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ref.InventoryMovementType;

import java.util.Optional;

@Repository
public interface InventoryMovementTypeRepository extends JpaRepository<InventoryMovementType, Long>{
    Optional<InventoryMovementType> findByCode(String code);
}