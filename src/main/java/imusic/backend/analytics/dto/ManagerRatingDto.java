package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ManagerRatingDto {
    private String managerName;
    private long totalOrders;
    private BigDecimal totalRevenue;
}
