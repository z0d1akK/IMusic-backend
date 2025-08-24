package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

public class ProductRequestDto extends BaseRequestDto {
    private Long categoryId;
    private Boolean isActive;
    private Double minPrice;
    private Double maxPrice;
    private Integer minStockLevel;
    private Integer maxStockLevel;
}
