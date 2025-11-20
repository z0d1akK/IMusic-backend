package imusic.backend.dto.create.ops;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemCreateDto {
    @NotNull
    private Long cartId;
    @NotNull
    private Long productId;
    @NotNull
    @Min(1)
    private Integer quantity;
}

