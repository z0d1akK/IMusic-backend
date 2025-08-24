package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

public class CartItemRequestDto extends BaseRequestDto {
    private Long cartId;
    private Long clientId;
    private Long productId;
    private Integer minQuantity;
    private Integer maxQuantity;
}