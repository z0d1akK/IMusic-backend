package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CategorySalesDto {
    private String category;
    private long totalSold;
    private BigDecimal totalRevenue;
}
