package imusic.backend.dto.request.ops;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    private Long categoryId;
    private Float minPrice;
    private Float maxPrice;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    private Integer minWarehouseQuantity;
    private Integer maxWarehouseQuantity;
    @NotNull
    private Integer page;
    @NotNull
    private Integer size;
    private String sortBy;
    private String sortDirection;
    private List<String> filters;
    private String search;
}
