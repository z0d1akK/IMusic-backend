package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class TopClientDto {
    private String clientName;
    private BigDecimal totalSpent;
}
