package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopProductDto {
    private String productName;
    private long totalSold;
    private BigDecimal totalRevenue;
}
