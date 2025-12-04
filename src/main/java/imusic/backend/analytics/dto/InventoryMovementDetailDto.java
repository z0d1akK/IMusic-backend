package imusic.backend.analytics.dto;

import lombok.Data;

@Data
public class InventoryMovementDetailDto {
    private Long movementId;
    private String movementDate;
    private Long productId;
    private String productName;
    private String movementType;
    private long quantity;
    private String comment;
}
