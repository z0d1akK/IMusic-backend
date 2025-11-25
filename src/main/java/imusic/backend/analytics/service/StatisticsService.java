package imusic.backend.analytics.service;

import imusic.backend.analytics.dto.*;
import imusic.backend.analytics.repository.StatisticsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository repo;

    public OverviewStatsDto getOverviewStats(LocalDate startDate, LocalDate endDate) {
        Map<String, Object> map = repo.fetchOverviewStats();

        OverviewStatsDto dto = new OverviewStatsDto();
        dto.setTotalOrders(getLong(map, "total_orders"));
        dto.setTotalRevenue(getBigDecimal(map, "total_revenue"));
        dto.setTotalClients(getLong(map, "total_clients"));
        dto.setTotalProducts(getLong(map, "total_products"));
        dto.setActiveOrders(getLong(map, "active_orders"));
        dto.setLowStockProducts(getLong(map, "low_stock_products"));
        dto.setActiveUsers(getLong(map, "active_users"));
        dto.setAvgOrderValue(getBigDecimal(map, "avg_order_value"));
        return dto;
    }

    public List<SalesTrendDto> getSalesTrends(LocalDate start, LocalDate end, String groupBy) {
        start = start != null ? start : LocalDate.of(2000, 1, 1);
        end = end != null ? end : LocalDate.now();

        return repo.fetchSalesTrends(start, end)
                .stream().map(m -> {
                    SalesTrendDto dto = new SalesTrendDto();
                    dto.setPeriod((String) m.get("period"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<OrderStatusStatsDto> getOrderStatusStats() {
        return repo.fetchOrderStatusStats()
                .stream().map(m -> {
                    OrderStatusStatsDto dto = new OrderStatusStatsDto();
                    dto.setStatus((String) m.get("status"));
                    dto.setCount(getLong(m, "count"));
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<TopClientDto> getTopClients(int limit) {
        return repo.fetchTopClients(limit)
                .stream().map(m -> {
                    TopClientDto dto = new TopClientDto();
                    dto.setClientName((String) m.get("client_name"));
                    dto.setTotalSpent(getBigDecimal(m, "total_spent"));
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<TopProductDto> getTopProducts(int limit) {
        return repo.fetchTopProducts(limit)
                .stream().map(m -> {
                    TopProductDto dto = new TopProductDto();
                    dto.setProductName((String) m.get("product_name"));
                    dto.setTotalSold(getLong(m, "total_sold"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<InventoryMovementDto> getInventoryMovements(LocalDate start, LocalDate end) {
        return repo.fetchInventoryMovements(start, end)
                .stream().map(m -> {
                    InventoryMovementDto dto = new InventoryMovementDto();
                    dto.setMovementType((String) m.get("movement_type"));
                    dto.setMovementCount(getLong(m, "movement_count"));
                    dto.setTotalQuantity(getLong(m, "total_quantity"));
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<LowStockProductDto> getLowStockProducts() {
        return repo.fetchLowStockProducts()
                .stream().map(m -> {
                    LowStockProductDto dto = new LowStockProductDto();
                    dto.setProductName((String) m.get("product_name"));
                    dto.setStockQuantity(getInt(m, "stock_quantity"));
                    dto.setMinStockLevel(getInt(m, "min_stock_level"));
                    return dto;
                }).collect(Collectors.toList());
    }

    public List<ManagerRatingDto> getManagerRatings() {
        return repo.fetchManagerRatings()
                .stream().map(m -> {
                    ManagerRatingDto dto = new ManagerRatingDto();
                    dto.setManagerName((String) m.get("manager_name"));
                    dto.setTotalOrders(getLong(m, "total_orders"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                }).collect(Collectors.toList());
    }

    public Long getActiveUsersCount(int lastDays) {
        return repo.fetchActiveUsersCount(lastDays);
    }

    private long getLong(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).longValue();
        return Long.parseLong(v.toString());
    }

    private int getInt(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return 0;
        if (v instanceof Number) return ((Number) v).intValue();
        return Integer.parseInt(v.toString());
    }

    private BigDecimal getBigDecimal(Map<String, Object> m, String key) {
        Object v = m.get(key);
        if (v == null) return BigDecimal.ZERO;
        if (v instanceof BigDecimal) return (BigDecimal) v;
        return new BigDecimal(v.toString());
    }

    public List<SalesTrendDto> getManagerSalesTrend(
            Long managerId, LocalDate start, LocalDate end) {

        return repo.fetchManagerSalesTrend(managerId, start, end)
                .stream()
                .map(m -> {
                    SalesTrendDto dto = new SalesTrendDto();
                    dto.setPeriod((String) m.get("period"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<TopClientDto> getManagerTopClients(Long managerId, LocalDate start, LocalDate end, int limit) {
        start = start != null ? start : LocalDate.of(2000, 1, 1);
        end = end != null ? end : LocalDate.now();

        return repo.fetchManagerTopClients(managerId, start, end, limit)
                .stream()
                .map(m -> {
                    TopClientDto dto = new TopClientDto();
                    dto.setClientName((String) m.get("client_name"));
                    dto.setTotalSpent(getBigDecimal(m, "total_spent"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<TopProductDto> getManagerTopProducts(
            Long managerId, LocalDate start, LocalDate end, int limit) {

        start = start != null ? start : LocalDate.of(2000, 1, 1);
        end = end != null ? end : LocalDate.now();

        return repo.fetchManagerTopProducts(managerId, start, end, limit)
                .stream()
                .map(m -> {
                    TopProductDto dto = new TopProductDto();
                    dto.setProductName((String) m.get("product_name"));
                    dto.setTotalSold(getLong(m, "total_sold"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

}
