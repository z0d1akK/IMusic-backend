package imusic.backend.analytics.dto;

import lombok.Data;

@Data
public class InventoryMovementDto {
    private String movementType;
    private long movementCount;
    private long totalQuantity;
}

