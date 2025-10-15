package imusic.backend.dto.request.ops;

import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementRequestDto {
    private Long productId;
    private Long movementTypeId;
    private Integer quantity;
    private Long createdById;
    private Integer page;
    private Integer size;
    private String sortBy;
    private String sortDirection;
    private List<String> filters;
}
