package imusic.backend.dto.response.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductComparisonResponseDto {
    private List<ProductResponseDto> products;
    private List<ComparisonAttributeDto> attributes;
}