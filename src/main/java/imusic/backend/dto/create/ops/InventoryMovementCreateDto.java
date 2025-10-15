package imusic.backend.dto.create.ops;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InventoryMovementCreateDto {
    @NotNull
    private Long productId;
    @NotNull
    private Integer quantity;
    @NotNull
    private Long movementTypeId;
    @NotNull
    private Long createdById;
    private String comment;
}

