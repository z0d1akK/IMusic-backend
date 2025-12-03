package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductSeasonalityDto {
    private String period;
    private long totalSold;
    private BigDecimal totalRevenue;
}
