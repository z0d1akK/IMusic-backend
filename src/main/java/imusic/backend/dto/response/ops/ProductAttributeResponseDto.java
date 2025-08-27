package imusic.backend.dto.response.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeResponseDto {
    private Long id;
    private String value;
    private String defaultValue;
    private Long categoryAttributeId;
    private String categoryAttributeName;
    private Long productId;
    private String productName;
}

