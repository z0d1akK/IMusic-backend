package imusic.backend.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private List<String> errors;

    public static ValidationErrorResponse of(int status, List<String> errors) {
        return ValidationErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(status)
                .errors(errors)
                .build();
    }
}
