package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class SalesTrendDto {
    private String period;
    private BigDecimal totalRevenue;
}
