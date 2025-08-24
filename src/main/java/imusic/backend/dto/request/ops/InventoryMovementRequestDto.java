package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

public class InventoryMovementRequestDto extends BaseRequestDto {
    private Long productId;
    private Long movementTypeId;
    private Long createdById;
    private Boolean isActive;
}
