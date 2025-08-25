package imusic.backend.dto.request.ops;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@EqualsAndHashCode(callSuper=false)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusHistoryRequestDto {
    private Long orderId;
    private Long oldStatusId;
    private Long newStatusId;
    private Long changedById;
    private LocalDateTime changedFrom;
    private LocalDateTime changedTo;
    @NotNull
    private Integer page;
    @NotNull
    private Integer size;
    private String sortBy;
    private String sortDirection;
    private List<String> filters;
}

