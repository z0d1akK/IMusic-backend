package imusic.backend.service.ops;

import imusic.backend.dto.create.ops.OrderCreateDto;
import imusic.backend.dto.request.ops.OrderRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.OrderResponseDto;
import imusic.backend.dto.update.ops.OrderUpdateDto;

import java.util.List;

public interface OrderService {
    OrderResponseDto create(OrderCreateDto dto);
    OrderResponseDto update(Long id, OrderUpdateDto dto);
    void delete(Long id);
    OrderResponseDto getById(Long id);
    List<OrderResponseDto> getOrdersByClientId(Long clientId);
    List<OrderResponseDto> getAll();
    PageResponseDto<OrderResponseDto> getPagedOrders(OrderRequestDto request);
}
