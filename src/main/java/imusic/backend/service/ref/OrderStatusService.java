package imusic.backend.service.ref;

import imusic.backend.dto.request.ref.OrderStatusRequestDto;
import imusic.backend.dto.response.ref.OrderStatusResponseDto;
import imusic.backend.dto.create.ref.OrderStatusCreateDto;
import imusic.backend.dto.update.ref.OrderStatusUpdateDto;

import java.util.List;

public interface OrderStatusService {
    List<OrderStatusResponseDto> getAll();
    OrderStatusResponseDto getById(Long id);
    OrderStatusResponseDto create(OrderStatusCreateDto dto);
    OrderStatusResponseDto update(Long id, OrderStatusUpdateDto dto);
    void delete(Long id);
    List<OrderStatusResponseDto> getStatusesWithFilters(OrderStatusRequestDto request);
}
