package imusic.backend.dto.response.ops;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponseDto {
    private Long id;
    private Long clientId;
    private String clientName;
    private Long createdById;
    private String createdByName;
    private Long statusId;
    private String statusCode;
    private String statusName;
    private Long paymentStatusId;
    private String paymentStatusCode;
    private String paymentStatusName;
    private Long paymentMethodId;
    private String paymentMethodCode;
    private String paymentMethodName;
    private BigDecimal totalPrice;
    private String deliveryAddress;
    private LocalDateTime deliveryDate;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemResponseDto> items;
}

