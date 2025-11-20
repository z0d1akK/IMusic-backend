package imusic.backend.service.ref;

import imusic.backend.dto.create.ref.OrderStatusCreateDto;
import imusic.backend.dto.request.ref.OrderStatusRequestDto;
import imusic.backend.dto.response.ref.OrderStatusResponseDto;
import imusic.backend.dto.update.ref.OrderStatusUpdateDto;
import imusic.backend.entity.ref.OrderStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ref.OrderStatusMapper;
import imusic.backend.repository.ref.OrderStatusRepository;
import imusic.backend.service.impl.ref.OrderStatusServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderStatusServiceTest {

    @Mock
    private OrderStatusRepository repository;

    @Mock
    private OrderStatusMapper mapper;

    @InjectMocks
    private OrderStatusServiceImpl service;

    private OrderStatus entity;
    private OrderStatusResponseDto responseDto;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        entity = OrderStatus.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        responseDto = OrderStatusResponseDto.builder()
                .id(1L)
                .code("TEST_CODE")
                .name("Test Name")
                .build();
    }

    @Test
    @DisplayName("Get all order statuses")
    void testGetAll() {
        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<OrderStatusResponseDto> result = service.getAll();

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("Get order status by ID")
    void testGetById() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        OrderStatusResponseDto result = service.getById(1L);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Get by invalid ID should throw exception")
    void testGetByIdNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.getById(1L));
        assertTrue(exception.getMessage().contains("Статус заказа не найден"));
    }

    @Test
    @DisplayName("Create new order status")
    void testCreate() {
        OrderStatusCreateDto dto = OrderStatusCreateDto.builder()
                .code("TEST_CODE")
                .name("Test Name")
                .build();

        when(mapper.toEntity(dto)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        OrderStatusResponseDto result = service.create(dto);

        assertNotNull(result);
        assertEquals("Test Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update order status")
    void testUpdate() {
        OrderStatusUpdateDto dto = OrderStatusUpdateDto.builder()
                .code("NEW_CODE")
                .name("New Name")
                .build();

        when(repository.findById(1L)).thenReturn(Optional.of(entity));
        when(repository.save(entity)).thenReturn(entity);
        when(mapper.toResponse(entity)).thenReturn(
                OrderStatusResponseDto.builder()
                        .id(1L)
                        .code("NEW_CODE")
                        .name("New Name")
                        .build()
        );

        OrderStatusResponseDto result = service.update(1L, dto);

        assertNotNull(result);
        assertEquals("NEW_CODE", result.getCode());
        assertEquals("New Name", result.getName());
        verify(repository, times(1)).save(entity);
    }

    @Test
    @DisplayName("Update non-existent order status should throw exception")
    void testUpdateNotFound() {
        OrderStatusUpdateDto dto = new OrderStatusUpdateDto();
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.update(1L, dto));
        assertTrue(exception.getMessage().contains("Статус заказа не найден"));
    }

    @Test
    @DisplayName("Delete order status")
    void testDelete() {
        when(repository.findById(1L)).thenReturn(Optional.of(entity));

        service.delete(1L);

        verify(repository, times(1)).delete(entity);
    }

    @Test
    @DisplayName("Delete non-existent order status should throw exception")
    void testDeleteNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        AppException exception = assertThrows(AppException.class, () -> service.delete(1L));
        assertTrue(exception.getMessage().contains("Статус заказа не найден"));
    }

    @Test
    @DisplayName("Get order statuses with filters")
    void testGetStatusesWithFilters() {
        OrderStatusRequestDto request = OrderStatusRequestDto.builder()
                .page(0)
                .size(10)
                .code("TEST")
                .name("Test")
                .sortBy("name")
                .sortDirection("asc")
                .build();

        when(repository.findAll()).thenReturn(List.of(entity));
        when(mapper.toResponse(entity)).thenReturn(responseDto);

        List<OrderStatusResponseDto> result = service.getStatusesWithFilters(request);

        assertEquals(1, result.size());
        assertEquals("TEST_CODE", result.get(0).getCode());
        verify(repository, times(1)).findAll();
    }
}

