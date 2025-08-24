package imusic.backend.dto.update.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementUpdateDto {
    private Integer quantity;
    private Long movementTypeId;
    private Boolean isActive;
}
