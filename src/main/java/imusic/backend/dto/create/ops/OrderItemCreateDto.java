package imusic.backend.dto.create.ops;

import java.math.BigDecimal;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemCreateDto {
    @NotNull
    private Long productId;
    @NotNull
    private Long orderId;
    @NotNull
    private Integer quantity;
    @NotNull
    private BigDecimal unitPrice;
}