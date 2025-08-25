package imusic.backend.dto.create.ref;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusCreateDto{
    @NotBlank
    private String code;
    @NotBlank
    private String name;
}