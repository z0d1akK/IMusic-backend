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
public class ClientCreateDto {
    @NotNull
    private Long userId;
    @NotBlank
    private String companyName;
    @Email
    private String email;
    @NotBlank
    private String phone;
    @NotBlank
    private String address;
    @NotBlank
    private String inn;
    @NotBlank
    private String contactPerson;
}

