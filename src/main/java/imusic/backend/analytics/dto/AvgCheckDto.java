package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AvgCheckDto {
    private Long clientId;
    private String clientName;
    private BigDecimal avgCheck;
}
