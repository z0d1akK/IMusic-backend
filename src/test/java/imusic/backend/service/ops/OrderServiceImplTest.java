package imusic.backend.service.ops;

import imusic.backend.dto.response.ops.OrderResponseDto;
import imusic.backend.dto.update.ops.OrderUpdateDto;
import imusic.backend.entity.ops.*;
import imusic.backend.entity.ref.OrderStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.OrderItemMapper;
import imusic.backend.mapper.ops.OrderMapper;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ops.*;
import imusic.backend.mapper.resolver.ref.OrderStatusResolver;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.repository.ops.OrderRepository;
import imusic.backend.repository.ops.UserRepository;
import imusic.backend.repository.ref.OrderStatusRepository;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.impl.ops.OrderServiceImpl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceImplTest {

    @Mock private OrderRepository orderRepository;
    @Mock private OrderMapper orderMapper;
    @Mock private OrderItemMapper orderItemMapper;
    @Mock private OrderResolver orderResolver;
    @Mock private ProductResolver productResolver;
    @Mock private ClientResolver clientResolver;
    @Mock private UserResolver userResolver;
    @Mock private OrderStatusResolver orderStatusResolver;
    @Mock private OrderStatusHistoryService orderStatusHistoryService;
    @Mock private InventoryMovementService inventoryMovementService;
    @Mock private OrderStatusRepository orderStatusRepository;
    @Mock private AuthService authService;
    @Mock private UserRepository userRepository;
    @Mock private UserMapper userMapper;
    @Mock private RoleResolver roleResolver;
    @Mock private UserStatusResolver userStatusResolver;

    @InjectMocks
    private OrderServiceImpl service;

    public OrderServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getById — успешно возвращает заказ")
    void testGetById() {
        Order order = new Order();
        order.setId(1L);

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderMapper.toResponse(order)).thenReturn(dto);

        OrderResponseDto result = service.getById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    @DisplayName("getById — выбрасывает AppException если заказ не найден")
    void testGetByIdNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(AppException.class, () -> service.getById(1L));
    }

    @Test
    @DisplayName("getAll — возвращает список заказов")
    void testGetAll() {
        Order order = new Order();
        order.setId(1L);

        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(1L);

        when(orderRepository.findAll()).thenReturn(List.of(order));
        when(orderMapper.toResponse(order)).thenReturn(dto);

        List<OrderResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    @DisplayName("delete — успешно удаляет заказ")
    void testDeleteSuccess() {
        when(orderRepository.existsById(1L)).thenReturn(true);
        doNothing().when(orderStatusHistoryService).deleteHistoryByOrderId(1L);
        doNothing().when(orderRepository).deleteById(1L);

        service.delete(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete — выбрасывает исключение если заказ не найден")
    void testDeleteNotFound() {
        when(orderRepository.existsById(1L)).thenReturn(false);
        assertThrows(AppException.class, () -> service.delete(1L));
    }

    @Test
    @DisplayName("update — успешно обновляет заказ и фиксирует историю статусов")
    void testUpdateOrder() {

        Order existing = new Order();
        existing.setId(1L);

        OrderStatus oldStatus = new OrderStatus();
        oldStatus.setId(1L);
        oldStatus.setCode("NEW");
        existing.setStatus(oldStatus);

        OrderStatus newStatus = new OrderStatus();
        newStatus.setId(2L);
        newStatus.setCode("DONE");

        User user = new User();
        user.setId(10L);

        OrderUpdateDto dto = new OrderUpdateDto();
        dto.setStatusId(2L);
        dto.setDeliveryAddress("new addr");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(authService.getCurrentUser()).thenReturn(null);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);
        when(orderStatusResolver.resolve(2L)).thenReturn(newStatus);
        when(orderRepository.save(existing)).thenReturn(existing);

        OrderResponseDto response = new OrderResponseDto();
        response.setId(1L);
        when(orderMapper.toResponse(existing)).thenReturn(response);

        OrderResponseDto result = service.update(1L, dto);

        assertEquals(1L, result.getId());
        verify(orderStatusHistoryService, times(1))
                .addStatusHistory(1L, 1L, 2L, 10L);
    }
}
