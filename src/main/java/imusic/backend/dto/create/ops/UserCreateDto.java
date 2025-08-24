package imusic.backend.dto.create.ops;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateDto {
    @NotBlank
    private String username;
    @NotBlank
    @Size(min = 6)
    private String password;
    @NotBlank
    private String fullName;
    @Email
    private String email;
    private String avatarPath;
    @NotNull
    private Long roleId;
}
