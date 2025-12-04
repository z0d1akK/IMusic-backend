package imusic.backend.analytics.service;

import imusic.backend.analytics.dto.*;
import imusic.backend.analytics.repository.StatisticsRepository;
import imusic.backend.repository.ops.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsService {

    private final StatisticsRepository repo;
    private final ClientRepository clientRepository;

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
        start = normalizeStart(start);
        end = normalizeEnd(end);

        List<SalesTrendDto> list = repo.fetchSalesTrends(start, end, groupBy)
                .stream()
                .map(m -> {
                    SalesTrendDto dto = new SalesTrendDto(); dto.setPeriod((String) m.get("period"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .sorted(Comparator.comparing(SalesTrendDto::getPeriod))
                .collect(Collectors.toList());

        return limitPoints(list, 200);
    }

    public List<OrderStatusStatsDto> getOrderStatusStats(LocalDate start, LocalDate end) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchOrderStatusStats(start, end)
                .stream()
                .map(m -> {
                    OrderStatusStatsDto dto = new OrderStatusStatsDto();
                    dto.setStatus((String) m.get("status"));
                    dto.setCount(getLong(m, "count")); return dto;
                })
                .collect(Collectors.toList());
    }

    public List<TopClientDto> getTopClients(LocalDate start, LocalDate end, int limit) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchTopClients(start, end, limit)
                .stream()
                .map(m -> {
                    TopClientDto dto = new TopClientDto();
                    dto.setClientName((String) m.get("client_name"));
                    dto.setTotalSpent(getBigDecimal(m, "total_spent"));
                    return dto;
                })
                .sorted(Comparator.comparing(TopClientDto::getTotalSpent).reversed())
                .collect(Collectors.toList());
    }

    public List<TopProductDto> getTopProducts(LocalDate start, LocalDate end, int limit) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchTopProducts(limit, start, end)
                .stream()
                .map(m -> {
                    TopProductDto dto = new TopProductDto();
                    dto.setProductName((String) m.get("product_name"));
                    dto.setProductId(getLong(m, "product_id"));
                    dto.setTotalSold(getLong(m, "total_sold"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .sorted(Comparator.comparing(TopProductDto::getTotalRevenue).reversed())
                .collect(Collectors.toList());
    }

    public List<InventoryMovementDto> getInventoryMovements(LocalDate start, LocalDate end, int limit) {
        return repo.fetchInventoryMovements(start, end)
                .stream()
                .map(m -> {
                    InventoryMovementDto dto = new InventoryMovementDto();
                    dto.setMovementType((String) m.get("movement_type"));
                    dto.setMovementCount(getLong(m, "movement_count"));
                    dto.setTotalQuantity(getLong(m, "total_quantity"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<LowStockProductDto> getLowStockProducts(LocalDate start, LocalDate end) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchLowStockProducts(start, end)
                .stream()
                .map(m -> {
                    LowStockProductDto dto = new LowStockProductDto();
                    dto.setProductId(getLong(m, "product_id"));
                    dto.setProductName((String) m.get("product_name"));
                    dto.setStockQuantity(getInt(m, "stock_quantity"));
                    dto.setMinStockLevel(getInt(m, "min_stock_level"));
                    return dto;
                })
                .sorted(Comparator.comparing(LowStockProductDto::getStockQuantity))
                .collect(Collectors.toList());
    }

    public List<ManagerRatingDto> getManagerRatings(LocalDate start, LocalDate end, int limit) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchManagerRatings(start, end, limit)
                .stream()
                .map(m -> {
                    ManagerRatingDto dto = new ManagerRatingDto();
                    dto.setManagerName((String) m.get("manager_name"));
                    dto.setTotalOrders(getLong(m, "total_orders"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .sorted(Comparator.comparing(ManagerRatingDto::getTotalRevenue).reversed())
                .collect(Collectors.toList());
    }

    public Long getActiveUsersCount(int lastDays) {
        return repo.fetchActiveUsersCount(lastDays);
    }

    public List<ProductSeasonalityDto> getProductSeasonality(Long productId, LocalDate start, LocalDate end, String groupBy, int limit) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        List<ProductSeasonalityDto> list = repo.fetchProductSeasonality(productId, start, end, groupBy)
                .stream()
                .map(m -> {
                    ProductSeasonalityDto dto = new ProductSeasonalityDto();
                    dto.setPeriod((String) m.get("period"));
                    dto.setTotalSold(getLong(m, "total_sold"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .sorted(Comparator.comparing(ProductSeasonalityDto::getPeriod))
                .collect(Collectors.toList());

        return limitPoints(list, 200);
    }

    public List<ProductSeasonalityDto> getManagerProductSeasonality(Long managerId, Long productId, LocalDate start, LocalDate end, String groupBy, int limit) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        List<ProductSeasonalityDto> list = repo.fetchManagerProductSeasonality(managerId, productId, start, end, groupBy)
                .stream()
                .map(m -> {
                    ProductSeasonalityDto dto = new ProductSeasonalityDto();
                    dto.setPeriod((String) m.get("period"));
                    dto.setTotalSold(getLong(m, "total_sold"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .sorted(Comparator.comparing(ProductSeasonalityDto::getPeriod))
                .collect(Collectors.toList());

        return limitPoints(list, 200);
    }

    public List<CategorySalesDto> getCategorySales(LocalDate start, LocalDate end, int limit) {
        return repo.fetchCategorySales(start, end, limit)
                .stream()
                .map(m -> {
                    CategorySalesDto dto = new CategorySalesDto();
                    dto.setCategory((String) m.get("category"));
                    dto.setTotalSold(getLong(m, "total_sold"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<CategorySalesDto> getManagerCategorySales(Long managerId, LocalDate start, LocalDate end, int limit) {
        return repo.fetchManagerCategorySales(managerId, start, end, limit)
                .stream()
                .map(m -> {
                    CategorySalesDto dto = new CategorySalesDto();
                    dto.setCategory((String) m.get("category"));
                    dto.setTotalSold(getLong(m, "total_sold"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<SalesTrendDto> getManagerSalesTrend(Long managerId, LocalDate start, LocalDate end, String groupBy, int limit) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        List<SalesTrendDto> list = repo.fetchManagerSalesTrend(managerId, start, end, groupBy)
                .stream()
                .map(m -> {
                    SalesTrendDto dto = new SalesTrendDto();
                    dto.setPeriod((String) m.get("period"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .collect(Collectors.toList());

        return limitPoints(list, 200);
    }

    public List<TopClientDto> getManagerTopClients(Long managerId, LocalDate start, LocalDate end, int limit) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchManagerTopClients(managerId, start, end, limit)
                .stream()
                .map(m -> {
                    TopClientDto dto = new TopClientDto();
                    dto.setClientName((String) m.get("client_name"));
                    dto.setTotalSpent(getBigDecimal(m, "total_spent"));
                    return dto;
                })
                .sorted(Comparator.comparing(TopClientDto::getTotalSpent).reversed())
                .collect(Collectors.toList());
    }

    public List<TopProductDto> getManagerTopProducts(Long managerId, LocalDate start, LocalDate end, int limit) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchManagerTopProducts(managerId, start, end, limit)
                .stream()
                .map(m -> {
                    TopProductDto dto = new TopProductDto();
                    dto.setProductName((String) m.get("product_name"));
                    dto.setProductId(getLong(m, "product_id"));
                    dto.setTotalSold(getLong(m, "total_sold"));
                    dto.setTotalRevenue(getBigDecimal(m, "total_revenue"));
                    return dto;
                })
                .sorted(Comparator.comparing(TopProductDto::getTotalRevenue).reversed())
                .collect(Collectors.toList());
    }

    public List<InventoryMovementTrendDto> getInventoryMovementTrends(
            Long productId,
            Long categoryId,
            LocalDate start,
            LocalDate end,
            String groupBy,
            int limit
    ) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        List<InventoryMovementTrendDto> list = repo.fetchInventoryMovementTrends(
                        productId, categoryId, start, end, groupBy, limit)
                .stream()
                .map(m -> {
                    InventoryMovementTrendDto dto = new InventoryMovementTrendDto();
                    dto.setPeriod((String) m.get("period"));
                    dto.setIncoming(getLong(m, "incoming"));
                    dto.setOutgoing(getLong(m, "outgoing"));
                    return dto;
                })
                .sorted(Comparator.comparing(InventoryMovementTrendDto::getPeriod))
                .collect(Collectors.toList());

        return limitPoints(list, 200);
    }

    public List<InventoryMovementDetailDto> getInventoryMovementDetails(
            Long productId,
            Long categoryId,
            LocalDate start,
            LocalDate end,
            int limit
    ) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchInventoryMovementDetails(productId, categoryId, start, end, limit)
                .stream()
                .map(m -> {
                    InventoryMovementDetailDto dto = new InventoryMovementDetailDto();
                    dto.setMovementId(getLong(m, "movement_id"));
                    dto.setMovementDate((String) m.get("movement_date"));
                    dto.setProductId(getLong(m, "product_id"));
                    dto.setProductName((String) m.get("product_name"));
                    dto.setMovementType((String) m.get("movement_type"));
                    dto.setQuantity(getLong(m, "quantity"));
                    dto.setComment((String) m.get("comment"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<AvgCheckDto> getAvgChecks(LocalDate start, LocalDate end, int limit) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchAvgChecks(start, end, limit)
                .stream()
                .map(m -> {
                    AvgCheckDto dto = new AvgCheckDto();
                    dto.setClientId(getLong(m, "client_id"));
                    dto.setClientName((String) m.get("client_name"));
                    dto.setAvgCheck(getBigDecimal(m, "avg_check"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public List<AvgCheckDto> getManagerAvgChecks(
            Long managerId, LocalDate start, LocalDate end, int limit
    ) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        return repo.fetchManagerAvgChecks(managerId, start, end, limit)
                .stream()
                .map(m -> {
                    AvgCheckDto dto = new AvgCheckDto();
                    dto.setClientId(getLong(m, "client_id"));
                    dto.setClientName((String) m.get("client_name"));
                    dto.setAvgCheck(getBigDecimal(m, "avg_check"));
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public AvgCheckDetailsResponse getAvgCheckDetails(
            Long clientId,
            LocalDate start,
            LocalDate end
    ) {
        start = normalizeStart(start);
        end = normalizeEnd(end);

        List<AvgCheckDetailDto> orders = repo.fetchAvgCheckDetails(clientId, start, end)
                .stream()
                .map(m -> {
                    AvgCheckDetailDto dto = new AvgCheckDetailDto();
                    dto.setOrderId(getLong(m, "order_id"));
                    dto.setOrderDate((String) m.get("order_date"));
                    dto.setTotalPrice(getBigDecimal(m, "total_price"));
                    dto.setStatus((String) m.get("status"));
                    return dto;
                })
                .collect(Collectors.toList());

        BigDecimal avgCheck = orders.stream()
                .map(AvgCheckDetailDto::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(
                        BigDecimal.valueOf(orders.isEmpty() ? 1 : orders.size()),
                        RoundingMode.HALF_UP
                );

        String clientName = clientRepository.findCompanyNameById(clientId);

        AvgCheckDetailsResponse response = new AvgCheckDetailsResponse();
        response.setClientId(clientId);
        response.setClientName(clientName);
        response.setAvgCheck(avgCheck);
        response.setOrders(orders);

        return response;
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

    private <T> List<T> limitPoints(List<T> data, int max) {
        if (data.size() <= max) return data;

        double ratio = (double) data.size() / max;
        List<T> result = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {
            if ((int) (i / ratio) > (result.size() - 1)) {
                result.add(data.get(i));
            }
        }
        return result;
    }

    private LocalDate normalizeStart(LocalDate start) {
        return start != null ? start : LocalDate.of(2000, 1, 1);
    }

    private LocalDate normalizeEnd(LocalDate end) {
        return end != null ? end : LocalDate.now();
    }
}

