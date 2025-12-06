package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopProductDto {
    private Long productId;
    private Long categoryId;
    private String productName;
    private long totalSold;
    private BigDecimal totalRevenue;
}
