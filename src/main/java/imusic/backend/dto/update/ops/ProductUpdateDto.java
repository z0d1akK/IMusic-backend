package imusic.backend.dto.update.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductUpdateDto {
    private String name;
    private String description;
    private Float price;
    private Long unitId;
    private Long categoryId;
    private Integer stockQuantity;
    private Integer warehouseQuantity;
    private Integer minStockLevel;
    private Boolean isActive;
    private String imagePath;
}
