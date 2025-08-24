package imusic.backend.dto.create.ops;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemCreateDto {
    /**
     * Если в эндпоинте используете путь вида /carts/{cartId}/items,
     * можно игнорировать это поле и брать cartId из PathVariable.
     */
    @NotNull
    private Long cartId;
    @NotNull
    private Long productId;
    @NotNull
    @Min(1)
    private Integer quantity;
}

