package imusic.backend.dto.create.ops;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    @NotBlank
    private String name;
    private String description;
    @NotNull
    private Float price;
    @NotNull
    private Long unitId;
    @NotNull
    private Long categoryId;
    @NotNull
    private Integer stockQuantity;
    @NotNull
    private Integer warehouseQuantity;
    private Integer minStockLevel;
    private String imagePath;
}
