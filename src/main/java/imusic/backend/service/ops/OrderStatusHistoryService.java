package imusic.backend.service.ops;

import imusic.backend.dto.response.ops.OrderStatusHistoryResponseDto;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

public interface OrderStatusHistoryService {
    List<OrderStatusHistoryResponseDto> getHistoryByOrderId(Long orderId);
    void addStatusHistory(Long orderId, Long oldStatusId, Long newStatusId, Long changedById);
    void deleteHistoryByOrderId(Long orderId);
}
