package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OverviewStatsDto {
    private long totalOrders;
    private BigDecimal totalRevenue;
    private long totalClients;
    private long totalProducts;
    private long activeOrders;
    private long lowStockProducts;
    private long activeUsers;
    private BigDecimal avgOrderValue;
}

