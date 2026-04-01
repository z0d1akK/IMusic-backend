package imusic.backend.dto.response.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ComparisonResponseDto {
    private Long id;
    private LocalDateTime createdAt;
    private int productCount;
}