package imusic.backend.dto.response.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponseDto {
    private Long id;
    private Long cartId;
    private Long productId;
    private String productName;
    private Integer quantity;
    private Float productPrice;
    private Integer productStockQuantity;
    private String productImagePath;
}

