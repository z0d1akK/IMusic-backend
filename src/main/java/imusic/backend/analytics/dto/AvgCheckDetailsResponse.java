package imusic.backend.analytics.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class AvgCheckDetailsResponse {
    private Long clientId;
    private String clientName;
    private BigDecimal avgCheck;
    private List<AvgCheckDetailDto> orders;
}
