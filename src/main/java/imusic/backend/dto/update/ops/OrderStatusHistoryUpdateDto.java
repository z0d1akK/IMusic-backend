package imusic.backend.dto.update.ops;

import lombok.Data;

@Data
public class OrderStatusHistoryUpdateDto {
    private Long newStatusId;
    private String comment;
}
