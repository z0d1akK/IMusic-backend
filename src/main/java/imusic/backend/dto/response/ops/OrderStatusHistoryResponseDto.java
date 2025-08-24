package imusic.backend.dto.response.ops;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryResponseDto {
    private Long id;
    private Long orderId;
    private Long oldStatusId;
    private String oldStatusCode;
    private String oldStatusName;
    private Long newStatusId;
    private String newStatusCode;
    private String newStatusName;
    private Long changedById;
    private String changedByName;
    private LocalDateTime changedAt;
    private String comment;
}


