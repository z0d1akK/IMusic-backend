package imusic.backend.dto.request.ops;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDto {
    private Long categoryId;
    private Boolean isActive;
    private Double minPrice;
    private Double maxPrice;
    private Integer minStockLevel;
    private Integer maxStockLevel;
    @NotNull
    private Integer page;
    @NotNull
    private Integer size;
    private String sortBy;
    private String sortDirection;
    private List<String> filters;
}