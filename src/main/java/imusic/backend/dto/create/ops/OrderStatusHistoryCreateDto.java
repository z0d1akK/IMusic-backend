package imusic.backend.dto.create.ops;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusHistoryCreateDto {
    @NotNull
    private Long orderId;
    @NotNull
    private Long oldStatusId;
    @NotNull
    private Long newStatusId;
    @NotNull
    private Long changedById;
    private String comment;
}

