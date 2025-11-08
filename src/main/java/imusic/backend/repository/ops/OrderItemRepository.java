package imusic.backend.repository.ops;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ops.OrderItem;

import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{
    @Modifying
    @Query("DELETE FROM OrderItem oi WHERE oi.product.id = :id")
    void deleteByProductId(@Param("id") Long id);
    Optional<OrderItem> findByProductId(Long productId);
}