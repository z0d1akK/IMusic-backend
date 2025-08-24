package imusic.backend.dto.update.ops;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateDto {
    private Long statusId;
    private Long paymentStatusId;
    private Long paymentMethodId;
    @Size(max = 4000)
    private String deliveryAddress;
    private LocalDateTime deliveryDate;
    private BigDecimal totalPrice;
    @Size(max = 4000)
    private String comment;
}
