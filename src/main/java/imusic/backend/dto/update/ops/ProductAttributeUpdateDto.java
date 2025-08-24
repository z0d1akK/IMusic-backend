package imusic.backend.dto.update.ops;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeUpdateDto {
    @NotNull
    private String value;
    @NotNull
    private Long categoryAttributeId;
    @NotNull
    private Long productId;
}

