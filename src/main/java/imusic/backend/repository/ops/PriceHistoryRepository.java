package imusic.backend.repository.ops;

import imusic.backend.entity.ops.PriceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface PriceHistoryRepository extends JpaRepository<PriceHistory, Long> {
    @Query(value = """
    SELECT 
        DATE(changed_at) as label,
        AVG(price) as price
    FROM ops.price_history
    WHERE product_id = :productId
      AND changed_at BETWEEN :from AND :to
    GROUP BY DATE(changed_at)
    ORDER BY DATE(changed_at)
""", nativeQuery = true)
    List<Object[]> getDailyPriceHistory(Long productId, LocalDateTime from, LocalDateTime to);

    @Query(value = """
    SELECT 
        TO_CHAR(changed_at, 'YYYY-MM') as label,
        AVG(price) as price
    FROM ops.price_history
    WHERE product_id = :productId
      AND changed_at BETWEEN :from AND :to
    GROUP BY TO_CHAR(changed_at, 'YYYY-MM')
    ORDER BY TO_CHAR(changed_at, 'YYYY-MM')
""", nativeQuery = true)
    List<Object[]> getMonthlyPriceHistory(Long productId, LocalDateTime from, LocalDateTime to);
}
