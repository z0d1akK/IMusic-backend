package imusic.backend.dto.create.ops;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class NotificationCreateDto {
    @NotBlank
    private String message;
    @NotNull
    private Long userId;
    @NotNull
    private Long typeId;
}

