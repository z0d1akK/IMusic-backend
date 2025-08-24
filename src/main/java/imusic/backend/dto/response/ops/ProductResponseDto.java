package imusic.backend.dto.response.ops;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDto {
    private Long id;
    private String name;
    private String description;
    private Float price;
    private Long unitId;
    private String unitName;
    private Long categoryId;
    private String categoryName;
    private Integer stockQuantity;
    private Integer warehouseQuantity;
    private Integer minStockLevel;
    private Boolean isActive;
    private String imagePath;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
