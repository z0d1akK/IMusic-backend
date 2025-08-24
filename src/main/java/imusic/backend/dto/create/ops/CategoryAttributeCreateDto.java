package imusic.backend.dto.create.ops;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CategoryAttributeCreateDto {
    @NotBlank
    private String name;
    @NotNull
    private Long categoryId;
    private String defaultValue;
}
