package imusic.backend.dto.create.ops;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateDto {
    @NotNull
    private Long clientId;
    @NotNull
    private Long createdBy;
    @NotNull
    private Long statusId;
    @NotNull
    private Long paymentStatusId;
    @NotNull
    private Long paymentMethodId;
    @NotNull
    private String deliveryAddress;
    @NotNull
    private LocalDateTime deliveryDate;
    private String comment;
    @NotEmpty
    private List<OrderItemCreateDto> items;
}
