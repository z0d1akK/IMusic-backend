package imusic.backend.dto.create.ref;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusCreateDto {
    @NotBlank
    private String code;
    @NotBlank
    private String name;
}