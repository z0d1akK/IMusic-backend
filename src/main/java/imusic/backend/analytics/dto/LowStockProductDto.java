package imusic.backend.analytics.dto;

import lombok.Data;

@Data
public class LowStockProductDto {
    private Long productId;
    private String productName;
    private int stockQuantity;
    private int minStockLevel;
}
