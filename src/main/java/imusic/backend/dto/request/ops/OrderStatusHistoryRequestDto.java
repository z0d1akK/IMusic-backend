package imusic.backend.dto.request.ops;

import imusic.backend.dto.request.BaseRequestDto;

import java.time.LocalDateTime;

public class OrderStatusHistoryRequestDto extends BaseRequestDto {
    private Long orderId;
    private Long oldStatusId;
    private Long newStatusId;
    private Long changedById;
    private LocalDateTime changedFrom;
    private LocalDateTime changedTo;
}

