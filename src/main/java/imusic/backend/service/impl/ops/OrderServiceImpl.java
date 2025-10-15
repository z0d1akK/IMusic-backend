package imusic.backend.service.impl.ops;

import imusic.backend.dto.create.ops.OrderCreateDto;
import imusic.backend.dto.request.ops.OrderRequestDto;
import imusic.backend.dto.response.ops.OrderResponseDto;
import imusic.backend.dto.update.ops.OrderUpdateDto;
import imusic.backend.entity.ops.Order;
import imusic.backend.entity.ops.OrderItem;
import imusic.backend.entity.ref.OrderStatus;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.OrderItemMapper;
import imusic.backend.mapper.ops.OrderMapper;
import imusic.backend.mapper.resolver.ops.ClientResolver;
import imusic.backend.mapper.resolver.ops.OrderResolver;
import imusic.backend.mapper.resolver.ops.ProductResolver;
import imusic.backend.mapper.resolver.ops.UserResolver;
import imusic.backend.mapper.resolver.ref.*;
import imusic.backend.repository.ops.OrderRepository;
import imusic.backend.repository.ref.OrderStatusRepository;
import imusic.backend.service.ops.InventoryMovementService;
import imusic.backend.service.ops.OrderService;
import imusic.backend.service.ops.OrderStatusHistoryService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderResolver orderResolver;
    private final ProductResolver productResolver;
    private final ClientResolver clientResolver;
    private final UserResolver userResolver;
    private final OrderStatusResolver orderStatusResolver;
    private final OrderStatusHistoryService orderStatusHistoryService;
    private final InventoryMovementService inventoryMovementService;
    private final OrderStatusRepository orderStatusRepository;

    @Override
    public OrderResponseDto getById(Long id) {
        return orderRepository.findById(id)
                .map(orderMapper::toResponse)
                .orElseThrow(() -> new AppException("Заказ не найден, ID: " + id));
    }

    @Override
    public List<OrderResponseDto> getOrdersByClientId(Long clientId) {
        return orderRepository.findAll().stream()
                .filter(order -> order.getClient().getId().equals(clientId))
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrderResponseDto> getAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponseDto create(OrderCreateDto dto) {
        Order order = orderMapper.toEntity(dto, clientResolver, userResolver, orderStatusResolver);

        if (order.getStatus() == null) {
            OrderStatus newStatus = orderStatusRepository.getById(1L);
            order.setStatus(newStatus);
        }

        List<OrderItem> items = dto.getItems().stream()
                .map(itemDto -> orderItemMapper.toEntity(itemDto, orderResolver, productResolver))
                .peek(item -> item.setOrder(order))
                .collect(Collectors.toList());

        order.setItems(items);

        BigDecimal total = BigDecimal.ZERO;

        for (OrderItem item : order.getItems()) {
            if (item.getProduct() == null || item.getProduct().getPrice() == null) {
                throw new AppException("Товар или его цена не указаны в позиции заказа");
            }
            BigDecimal unitPrice = BigDecimal.valueOf(item.getProduct().getPrice());
            item.setUnitPrice(unitPrice);

            BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(item.getQuantity()));
            total = total.add(lineTotal);
        }
        order.setTotalPrice(total);

        Order saved = orderRepository.save(order);

        orderStatusHistoryService.addStatusHistory(
                saved.getId(),
                1L,
                saved.getStatus().getId(),
                saved.getCreatedBy().getId()
        );

        for (OrderItem item : saved.getItems()) {
            inventoryMovementService.createInventoryMovement(
                    item.getProduct(),
                    item.getQuantity(),
                    "OUTCOME",
                    "Продажа по заказу ID: " + saved.getId()
            );
        }

        return orderMapper.toResponse(saved);
    }

    @Override
    public OrderResponseDto update(Long id, OrderUpdateDto dto) {
        Order existing = orderRepository.findById(id)
                .orElseThrow(() -> new AppException("Заказ не найден, ID: " + id));
        if (dto.getStatusId() != null && !dto.getStatusId().equals(existing.getStatus().getId())) {
            OrderStatus newStatus = orderStatusResolver.resolve(dto.getStatusId());

            orderStatusHistoryService.addStatusHistory(
                    existing.getId(),
                    existing.getStatus().getId(),
                    newStatus.getId(),
                    dto.getCreatedById()
            );

            existing.setStatus(newStatus);

            if (newStatus.getCode().equals("CANCELLED") || newStatus.getCode().equals("RETURNED")) {
                for (OrderItem item : existing.getItems()) {
                    inventoryMovementService.createInventoryMovement(
                            item.getProduct(),
                            item.getQuantity(),
                            newStatus.getCode().equals("RETURNED") ? "INCOME" : "RETURN_TO_STOCK",
                            "Возврат по заказу ID: " + existing.getId()
                    );
                }
            }
        }

        orderMapper.updateEntity(dto, orderStatusResolver, existing);
        return orderMapper.toResponse(orderRepository.save(existing));
    }

    @Override
    public void delete(Long id) {
        if (!orderRepository.existsById(id)) {
            throw new AppException("Заказ не найден, ID: " + id);
        }

        orderStatusHistoryService.deleteHistoryByOrderId(id);
        orderRepository.deleteById(id);
    }

//
//    @Override
//    public List<OrderResponseDto> getOrdersWithFilters(OrderRequestDto request) {
//
//        List<Order> orders = orderRepository.findAll();
//        if (request.get() != null) {
//            products = products.stream()
//                    .filter(p -> p.getCategory() != null && p.getCategory().getId().equals(request.getCategoryId()))
//                    .collect(Collectors.toList());
//        }
//
//        orders = orders.stream()
//                .filter(o -> orderId == null || o.getId().equals(orderId))
//                .filter(o -> clientId == null || (o.getClient() != null && o.getClient().getId().equals(clientId)))
//                .filter(o -> statusId == null || (o.getStatus() != null && o.getStatus().getId().equals(statusId)))
//                .filter(o -> createdById == null || (o.getCreatedBy() != null && o.getCreatedBy().getId().equals(createdById)))
//                .filter(o -> minTotalPrice == null || (o.getTotalPrice() != null && o.getTotalPrice().compareTo(BigDecimal.valueOf(minTotalPrice)) >= 0))
//                .filter(o -> maxTotalPrice == null || (o.getTotalPrice() != null && o.getTotalPrice().compareTo(BigDecimal.valueOf(maxTotalPrice)) <= 0))
//                .filter(o -> fromDate == null || (o.getCreatedAt() != null && o.getCreatedAt().isAfter(LocalDateTime.parse(fromDate))))
//                .filter(o -> toDate == null || (o.getCreatedAt() != null && o.getCreatedAt().isBefore(LocalDateTime.parse(toDate))))
//                .filter(o -> hasItems == null || (hasItems.equals(!o.getItems().isEmpty())))
//                .collect(Collectors.toList());
//
//        orders.sort(getOrderSortComparator(sortBy, sortDir));
//
//        int fromIndex = Math.max(0, page * size);
//        int toIndex = Math.min(fromIndex + size, orders.size());
//
//        return orders.subList(fromIndex, toIndex).stream()
//                .map(orderMapper::toResponse)
//                .collect(Collectors.toList());
//    }
//
//
//    private Comparator<Order> getOrderSortComparator(String sortBy, String sortDir) {
//        Comparator<Order> comparator = switch (sortBy != null ? sortBy : "") {
//            case "createdAt" -> Comparator.comparing(Order::getCreatedAt, Comparator.nullsLast(Comparator.naturalOrder()));
//            case "totalPrice" -> Comparator.comparing(Order::getTotalPrice, Comparator.nullsLast(BigDecimal::compareTo));
//            case "status" -> Comparator.comparing(o -> o.getStatus() != null ? safeString(o.getStatus().getName()) : "");
//            case "client" -> Comparator.comparing(o -> o.getClient() != null ? safeString(o.getClient().getCompanyName()) : "");
//            default -> Comparator.comparing(Order::getId);
//        };
//
//        if ("desc".equalsIgnoreCase(sortDir)) {
//            comparator = comparator.reversed();
//        }
//
//        return comparator;
//    }
//
//    private String safeString(String value) {
//        return value != null ? value.toLowerCase() : "";
//    }
//}
}
