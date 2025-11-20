package imusic.backend.analytics.dto;

import lombok.Data;

@Data
public class OrderStatusStatsDto {
    private String status;
    private long count;
}
