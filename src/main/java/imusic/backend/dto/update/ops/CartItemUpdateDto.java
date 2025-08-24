package imusic.backend.dto.update.ops;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemUpdateDto {
    @NotNull
    private Long productId;
    @NotNull
    private Integer quantity;
}
