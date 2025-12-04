package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AvgCheckDetailDto {
    private Long orderId;
    private String orderDate;
    private BigDecimal totalPrice;
    private String status;
}
