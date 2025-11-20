package imusic.backend.analytics.service;

import imusic.backend.analytics.dto.*;
import imusic.backend.analytics.repository.StatisticsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private StatisticsRepository repo;

    @InjectMocks
    private StatisticsService service;

    @Test
    void testGetOverviewStats() {
        Map<String, Object> data = new HashMap<>();
        data.put("total_orders", 10L);
        data.put("total_revenue", BigDecimal.valueOf(500));
        data.put("total_clients", 3L);
        data.put("total_products", 20L);
        data.put("active_orders", 2L);
        data.put("low_stock_products", 1L);
        data.put("active_users", 4L);
        data.put("avg_order_value", BigDecimal.valueOf(50));

        when(repo.fetchOverviewStats()).thenReturn(data);

        OverviewStatsDto dto = service.getOverviewStats(null, null);

        assertThat(dto.getTotalOrders()).isEqualTo(10);
        assertThat(dto.getTotalRevenue()).isEqualTo(BigDecimal.valueOf(500));
        verify(repo).fetchOverviewStats();
    }

    @Test
    void testGetSalesTrends() {
        List<Map<String, Object>> list = List.of(
                Map.of("period", "2024-01", "total_revenue", BigDecimal.valueOf(1000))
        );

        when(repo.fetchSalesTrends(any(), any())).thenReturn(list);

        List<SalesTrendDto> result = service.getSalesTrends(null, null, "month");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getPeriod()).isEqualTo("2024-01");
        assertThat(result.get(0).getTotalRevenue()).isEqualTo(BigDecimal.valueOf(1000));
    }

    @Test
    void testGetOrderStatusStats() {
        when(repo.fetchOrderStatusStats()).thenReturn(
                List.of(Map.of("status", "PAID", "count", 7L))
        );

        var result = service.getOrderStatusStats();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo("PAID");
    }

    @Test
    void testGetTopClients() {
        when(repo.fetchTopClients(5)).thenReturn(
                List.of(Map.of("client_name", "John", "total_spent", BigDecimal.valueOf(300)))
        );

        var result = service.getTopClients(5);
        assertThat(result.get(0).getClientName()).isEqualTo("John");
    }

    @Test
    void testGetTopProducts() {
        when(repo.fetchTopProducts(5)).thenReturn(
                List.of(Map.of("product_name", "Product", "total_sold", 10L, "total_revenue", BigDecimal.valueOf(200)))
        );

        var result = service.getTopProducts(5);
        assertThat(result.get(0).getProductName()).isEqualTo("Product");
    }

    @Test
    void testGetInventoryMovements() {
        when(repo.fetchInventoryMovements(any(), any())).thenReturn(
                List.of(Map.of("movement_type", "IN", "movement_count", 5L, "total_quantity", 30L))
        );

        var result = service.getInventoryMovements(null, null);
        assertThat(result.get(0).getMovementType()).isEqualTo("IN");
    }

    @Test
    void testGetLowStockProducts() {
        when(repo.fetchLowStockProducts()).thenReturn(
                List.of(Map.of("product_name", "X", "stock_quantity", 1, "min_stock_level", 5))
        );

        var result = service.getLowStockProducts();
        assertThat(result.get(0).getStockQuantity()).isEqualTo(1);
    }

    @Test
    void testGetManagerRating() {
        when(repo.fetchManagerRatings()).thenReturn(
                List.of(Map.of("manager_name", "Mike", "total_orders", 2L, "total_revenue", BigDecimal.valueOf(220)))
        );

        var result = service.getManagerRatings();
        assertThat(result.get(0).getManagerName()).isEqualTo("Mike");
    }

    @Test
    void testGetActiveUsersCount() {
        when(repo.fetchActiveUsersCount(30)).thenReturn(15L);

        assertThat(service.getActiveUsersCount(30)).isEqualTo(15L);
    }
}

