package imusic.backend.analytics.repository;

import imusic.backend.entity.ops.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface StatisticsRepository extends JpaRepository<Order, Long> {

    @Query(value = """
        SELECT
            (SELECT COUNT(*) FROM ops.orders) AS total_orders,
            COALESCE(SUM(oi.total_price), 0) AS total_revenue,
            (SELECT COUNT(*) FROM ops.clients) AS total_clients,
            (SELECT COUNT(*) FROM ops.products) AS total_products,
            (SELECT COUNT(*) FROM ops.orders o
                JOIN ref.order_statuses s ON o.status_id = s.id
                WHERE s.code NOT IN ('DELIVERED', 'CANCELED')) AS active_orders,
            (SELECT COUNT(*) FROM ops.products p WHERE p.stock_quantity < p.min_stock_level) AS low_stock_products,
            (SELECT COUNT(*) FROM ops.users u WHERE NOT u.is_blocked AND u.is_deleted) AS active_users,
            ROUND(COALESCE(SUM(oi.total_price) / NULLIF(COUNT(DISTINCT o.id), 0), 0), 2) AS avg_order_value
        FROM ops.orders o
        JOIN ops.order_items oi ON oi.order_id = o.id;
    """, nativeQuery = true)
    Map<String, Object> fetchOverviewStats();

    @Query(value = """
    SELECT TO_CHAR(o.created_at, 'YYYY-MM-DD') AS period,
           COALESCE(SUM(oi.total_price), 0) AS total_revenue
    FROM ops.orders o
    JOIN ops.order_items oi ON oi.order_id = o.id
    WHERE o.created_at >= COALESCE(:startDate, o.created_at)
      AND o.created_at <= COALESCE(:endDate, o.created_at)
    GROUP BY TO_CHAR(o.created_at, 'YYYY-MM-DD')
    ORDER BY period
""", nativeQuery = true)
    List<Map<String, Object>> fetchSalesTrends(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );



    @Query(value = """
        SELECT s.name AS status, COUNT(o.id) AS count
        FROM ops.orders o
        JOIN ref.order_statuses s ON s.id = o.status_id
        GROUP BY s.name
        ORDER BY count DESC;
    """, nativeQuery = true)
    List<Map<String, Object>> fetchOrderStatusStats();

    @Query(value = """
        SELECT c.company_name AS client_name,
               COALESCE(SUM(oi.total_price), 0) AS total_spent
        FROM ops.clients c
        JOIN ops.orders o ON o.client_id = c.id
        JOIN ops.order_items oi ON oi.order_id = o.id
        GROUP BY c.id, c.company_name
        ORDER BY total_spent DESC
        LIMIT :limit;
    """, nativeQuery = true)
    List<Map<String, Object>> fetchTopClients(@Param("limit") int limit);

    @Query(value = """
        SELECT p.name AS product_name,
               COALESCE(SUM(oi.quantity), 0) AS total_sold,
               COALESCE(SUM(oi.total_price), 0) AS total_revenue
        FROM ops.products p
        JOIN ops.order_items oi ON oi.product_id = p.id
        GROUP BY p.id, p.name
        ORDER BY total_sold DESC
        LIMIT :limit;
    """, nativeQuery = true)
    List<Map<String, Object>> fetchTopProducts(@Param("limit") int limit);

    @Query(value = """
        SELECT t.name AS movement_type,
               COUNT(m.id) AS movement_count,
               COALESCE(SUM(m.quantity), 0) AS total_quantity
        FROM ops.inventory_movements m
        JOIN ref.inventory_movement_types t ON t.id = m.movement_type_id
        WHERE (:startDate IS NULL OR m.created_at >= :startDate)
          AND (:endDate IS NULL OR m.created_at <= :endDate)
        GROUP BY t.name
        ORDER BY t.name;
    """, nativeQuery = true)
    List<Map<String, Object>> fetchInventoryMovements(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query(value = """
        SELECT p.name AS product_name,
               p.stock_quantity,
               p.min_stock_level
        FROM ops.products p
        WHERE p.stock_quantity < p.min_stock_level
        ORDER BY p.stock_quantity ASC;
    """, nativeQuery = true)
    List<Map<String, Object>> fetchLowStockProducts();

    @Query(value = """
        SELECT u.full_name AS manager_name,
               COUNT(o.id) AS total_orders,
               COALESCE(SUM(oi.total_price), 0) AS total_revenue
        FROM ops.users u
        JOIN ops.orders o ON o.created_by = u.id
        JOIN ops.order_items oi ON oi.order_id = o.id
        WHERE u.role_id IN (
            SELECT r.id FROM ref.roles r WHERE r.code = 'MANAGER'
        )
        GROUP BY u.full_name
        ORDER BY total_revenue DESC;
    """, nativeQuery = true)
    List<Map<String, Object>> fetchManagerRatings();

    @Query(value = """
        SELECT COUNT(*)
        FROM ops.users
        WHERE last_login_at >= NOW() - INTERVAL :days DAY;
    """, nativeQuery = true)
    Long fetchActiveUsersCount(@Param("days") int days);

    @Query(value = """
    SELECT 
        TO_CHAR(o.created_at, 'YYYY-MM-DD') AS period,
        COALESCE(SUM(oi.total_price), 0) AS total_revenue
    FROM ops.orders o
    JOIN ops.order_items oi ON oi.order_id = o.id
    WHERE o.created_by = :managerId
      AND o.created_at BETWEEN :startDate AND :endDate
    GROUP BY period
    ORDER BY period;
""", nativeQuery = true)
    List<Map<String, Object>> fetchManagerSalesTrend(
            @Param("managerId") Long managerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );



    @Query(value = """
    SELECT 
        c.company_name AS client_name,
        COALESCE(SUM(oi.total_price),0) AS total_spent
    FROM ops.orders o
    JOIN ops.clients c ON c.id = o.client_id
    JOIN ops.order_items oi ON oi.order_id = o.id
    WHERE o.created_by = :managerId
      AND o.created_at >= :startDate
      AND o.created_at <= :endDate
    GROUP BY c.id
    ORDER BY total_spent DESC
    LIMIT :limit;
""", nativeQuery = true)
    List<Map<String, Object>> fetchManagerTopClients(
            @Param("managerId") Long managerId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit
    );



    @Query(value = """
    SELECT 
        p.name AS product_name,
        SUM(oi.quantity) AS total_sold,
        SUM(oi.total_price) AS total_revenue
    FROM ops.orders o
    JOIN ops.order_items oi ON oi.order_id = o.id
    JOIN ops.products p ON p.id = oi.product_id
    WHERE o.created_by = :managerId
    GROUP BY p.id
    ORDER BY total_sold DESC
    LIMIT :limit;
""", nativeQuery = true)
    List<Map<String, Object>> fetchManagerTopProducts(
            @Param("managerId") Long managerId,
            @Param("limit") int limit
    );

}
