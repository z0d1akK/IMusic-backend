package imusic.backend.service.impl.ops;

import imusic.backend.dto.create.ops.OrderStatusHistoryCreateDto;
import imusic.backend.dto.response.ops.OrderStatusHistoryResponseDto;
import imusic.backend.mapper.ops.OrderStatusHistoryMapper;
import imusic.backend.mapper.resolver.ops.OrderResolver;
import imusic.backend.mapper.resolver.ops.UserResolver;
import imusic.backend.mapper.resolver.ref.OrderStatusResolver;
import imusic.backend.repository.ops.OrderStatusHistoryRepository;
import imusic.backend.service.ops.OrderStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderStatusHistoryServiceImpl implements OrderStatusHistoryService {

    private final OrderStatusHistoryRepository historyRepository;
    private final OrderStatusHistoryMapper historyMapper;
    private final OrderResolver orderResolver;
    private final OrderStatusResolver orderStatusResolver;
    private final UserResolver userResolver;

    @Override
    @Cacheable(cacheNames = "orderHistory", key = "#orderId")
    @Transactional(readOnly = true)
    public List<OrderStatusHistoryResponseDto> getHistoryByOrderId(Long orderId) {
        return historyRepository.findByOrderId(orderId).stream()
                .map(historyMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @CacheEvict(cacheNames = "orderHistory", allEntries = true)
    public void addStatusHistory(Long orderId, Long oldStatusId, Long newStatusId, Long changedById, String comment) {
        OrderStatusHistoryCreateDto dto = new OrderStatusHistoryCreateDto();
        dto.setOrderId(orderId);
        dto.setOldStatusId(oldStatusId);
        dto.setNewStatusId(newStatusId);
        dto.setChangedById(changedById);
        dto.setComment(comment);

        historyRepository.save(historyMapper.toEntity(dto, orderResolver, orderStatusResolver, userResolver));
    }

    @Override
    @CacheEvict(cacheNames = "orderHistory", allEntries = true)
    public void deleteHistoryByOrderId(Long orderId) {
        historyRepository.deleteByOrderId(orderId);
    }
}
