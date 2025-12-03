package imusic.backend.analytics.service;

import imusic.backend.analytics.dto.*;
import imusic.backend.analytics.repository.StatisticsRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private StatisticsRepository repo;

    @InjectMocks
    private StatisticsService service;

    private final LocalDate start = LocalDate.of(2024,1,1);
    private final LocalDate end = LocalDate.of(2024,12,31);

    @Test
    void testGetOverviewStats() {
        Map<String, Object> data = Map.of(
                "total_orders", 10L,
                "total_revenue", BigDecimal.valueOf(500),
                "total_clients", 3L,
                "total_products", 20L,
                "active_orders", 2L,
                "low_stock_products", 1L,
                "active_users", 4L,
                "avg_order_value", BigDecimal.valueOf(50)
        );

        when(repo.fetchOverviewStats()).thenReturn(data);

        OverviewStatsDto dto = service.getOverviewStats(null, null);

        assertThat(dto.getTotalOrders()).isEqualTo(10);
        assertThat(dto.getTotalRevenue()).isEqualTo(BigDecimal.valueOf(500));
        verify(repo).fetchOverviewStats();
    }

    @Test
    void testGetTopClientsWithLimit() {
        when(repo.fetchTopClients(start, end, 5)).thenReturn(
                List.of(Map.of("client_name","John","total_spent",BigDecimal.valueOf(300)))
        );

        var list = service.getTopClients(start, end, 5);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getClientName()).isEqualTo("John");
    }

    @Test
    void testGetTopProductsWithLimit() {
        when(repo.fetchTopProducts(5, start, end)).thenReturn(
                List.of(Map.of("product_name","Guitar","product_id",1L,"total_sold",5L,"total_revenue",BigDecimal.valueOf(200)))
        );

        var list = service.getTopProducts(start, end, 5);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getProductName()).isEqualTo("Guitar");
        assertThat(list.get(0).getProductId()).isEqualTo(1L);
    }

    @Test
    void testGetManagerTopClients() {
        when(repo.fetchManagerTopClients(2L, start, end, 5)).thenReturn(
                List.of(Map.of("client_name","Alice","total_spent",BigDecimal.valueOf(150)))
        );

        var list = service.getManagerTopClients(2L, start, end, 5);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getClientName()).isEqualTo("Alice");
    }

    @Test
    void testGetManagerTopProducts() {
        when(repo.fetchManagerTopProducts(2L, start, end, 5)).thenReturn(
                List.of(Map.of("product_name","Piano","product_id",10L,"total_sold",3L,"total_revenue",BigDecimal.valueOf(500)))
        );

        var list = service.getManagerTopProducts(2L, start, end, 5);
        assertThat(list).hasSize(1);
        assertThat(list.get(0).getProductName()).isEqualTo("Piano");
        assertThat(list.get(0).getProductId()).isEqualTo(10L);
    }

    @Test
    void testGetSalesTrends() {
        when(repo.fetchSalesTrends(start, end, "month")).thenReturn(
                List.of(Map.of("period","2024-01","total_revenue",BigDecimal.valueOf(1000)))
        );

        var list = service.getSalesTrends(start, end, "month");
        assertThat(list.get(0).getPeriod()).isEqualTo("2024-01");
    }

    @Test
    void testGetActiveUsersCount() {
        when(repo.fetchActiveUsersCount(30)).thenReturn(15L);
        assertThat(service.getActiveUsersCount(30)).isEqualTo(15L);
    }
}
