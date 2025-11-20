package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.OrderStatusHistoryCreateDto;
import imusic.backend.dto.response.ops.OrderStatusHistoryResponseDto;
import imusic.backend.entity.ops.OrderStatusHistory;
import imusic.backend.mapper.ops.OrderStatusHistoryMapper;
import imusic.backend.mapper.resolver.ops.OrderResolver;
import imusic.backend.mapper.resolver.ops.UserResolver;
import imusic.backend.mapper.resolver.ref.OrderStatusResolver;
import imusic.backend.repository.ops.OrderStatusHistoryRepository;
import imusic.backend.service.impl.ops.OrderStatusHistoryServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusHistoryServiceImplTest {

    @Mock
    private OrderStatusHistoryRepository historyRepository;

    @Mock
    private OrderStatusHistoryMapper historyMapper;

    @Mock
    private OrderResolver orderResolver;

    @Mock
    private OrderStatusResolver orderStatusResolver;

    @Mock
    private UserResolver userResolver;

    @InjectMocks
    private OrderStatusHistoryServiceImpl service;

    @Test
    @DisplayName("Получение истории статусов заказа по ID")
    void getHistoryByOrderId() {
        OrderStatusHistory entity = OrderStatusHistory.builder()
                .id(1L)
                .changedAt(LocalDateTime.now())
                .build();

        OrderStatusHistoryResponseDto dto = OrderStatusHistoryResponseDto.builder()
                .id(1L)
                .changedAt(entity.getChangedAt())
                .build();

        when(historyRepository.findByOrderId(10L)).thenReturn(List.of(entity));
        when(historyMapper.toResponse(entity)).thenReturn(dto);

        List<OrderStatusHistoryResponseDto> result = service.getHistoryByOrderId(10L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(1L);

        verify(historyRepository).findByOrderId(10L);
        verify(historyMapper).toResponse(entity);
    }

    @Test
    @DisplayName("Добавление новой записи в историю статусов")
    void addStatusHistory() {
        OrderStatusHistoryCreateDto dto = new OrderStatusHistoryCreateDto();
        dto.setOrderId(1L);
        dto.setOldStatusId(2L);
        dto.setNewStatusId(3L);
        dto.setChangedById(4L);

        OrderStatusHistory entity = OrderStatusHistory.builder()
                .id(99L)
                .build();

        when(historyMapper.toEntity(dto, orderResolver, orderStatusResolver, userResolver))
                .thenReturn(entity);

        service.addStatusHistory(1L, 2L, 3L, 4L);

        verify(historyRepository).save(entity);
    }

    @Test
    @DisplayName("Удаление истории статусов по ID заказа")
    void deleteHistoryByOrderId() {
        doNothing().when(historyRepository).deleteByOrderId(10L);

        service.deleteHistoryByOrderId(10L);

        verify(historyRepository).deleteByOrderId(10L);
    }
}

