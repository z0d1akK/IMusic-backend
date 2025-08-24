package imusic.backend.dto.update.ops;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemUpdateDto {
    private Long orderId;
    private Long productId;
    @Min(1)
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
}
