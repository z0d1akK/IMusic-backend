package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

public class OrderItemRequestDto extends BaseRequestDto {
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private Double totalPrice;
}

