package imusic.backend.controller.ops;

import imusic.backend.dto.response.ops.OrderStatusHistoryResponseDto;
import imusic.backend.service.ops.OrderStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-status-history")
@RequiredArgsConstructor
@Validated
public class OrderStatusHistoryController {

    private final OrderStatusHistoryService orderStatusHistoryService;

    @GetMapping("/{orderId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<OrderStatusHistoryResponseDto>> getHistoryByOrderId(@PathVariable Long orderId) {
        return ResponseEntity.ok(orderStatusHistoryService.getHistoryByOrderId(orderId));
    }

//    @PostMapping("/filter")
//    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
//    public ResponseEntity<List<OrderStatusHistoryResponseDto>> getFilteredHistory(
//            @RequestBody @Validated OrderStatusHistoryRequestDto request
//    ) {
//        List<OrderStatusHistoryResponseDto> response = orderStatusHistoryService.getHistoryByOrderId(
//                request.getOrderId()
//        );
//        return ResponseEntity.ok(response);
//    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> deleteHistoryByOrderId(@PathVariable Long orderId) {
        orderStatusHistoryService.deleteHistoryByOrderId(orderId);
        return ResponseEntity.noContent().build();
    }
}
