package imusic.backend.analytics.dto;

import lombok.Data;

@Data
public class LowStockProductDto {
    private String productName;
    private int stockQuantity;
    private int minStockLevel;
}
