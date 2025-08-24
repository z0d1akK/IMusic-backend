package imusic.backend.dto.create.ops;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeCreateDto {
    @NotBlank
    private String value;
    @NotNull
    private Long categoryAttributeId;
    @NotNull
    private Long productId;
}
