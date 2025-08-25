package imusic.backend.repository.ref;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import imusic.backend.entity.ref.OrderStatus;

@Repository
public interface OrderStatusRepository extends JpaRepository<OrderStatus, Long>{}