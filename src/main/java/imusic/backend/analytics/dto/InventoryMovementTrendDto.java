package imusic.backend.analytics.dto;

import lombok.Data;

@Data
public class InventoryMovementTrendDto {
    private String period;
    private long incoming;
    private long outgoing;
}

