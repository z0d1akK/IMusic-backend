package imusic.backend.dto.response.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAttributeResponseDto {
    private Long id;
    private String name;
    private Long categoryId;
    private String categoryCode;
    private String categoryName;
    private String defaultValue;
}

