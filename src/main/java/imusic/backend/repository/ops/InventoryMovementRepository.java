package imusic.backend.repository.ops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ops.InventoryMovement;

@Repository
public interface InventoryMovementRepository extends JpaRepository<InventoryMovement, Long>{
    @Modifying
    @Query("DELETE FROM InventoryMovement im WHERE im.product.id = :id")
    void deleteByProductId(@Param("id") Long id);
}