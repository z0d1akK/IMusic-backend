package imusic.backend.controller.ref;

import imusic.backend.dto.request.ref.OrderStatusRequestDto;
import imusic.backend.dto.response.ref.OrderStatusResponseDto;
import imusic.backend.dto.create.ref.OrderStatusCreateDto;
import imusic.backend.dto.update.ref.OrderStatusUpdateDto;
import imusic.backend.service.ref.OrderStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ref/order-statuses")
@RequiredArgsConstructor
public class OrderStatusController {

    private final OrderStatusService service;

    @GetMapping
    public ResponseEntity<List<OrderStatusResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderStatusResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderStatusResponseDto> create(@RequestBody OrderStatusCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<OrderStatusResponseDto> update(@PathVariable Long id, @RequestBody OrderStatusUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<OrderStatusResponseDto>> getWithFilters(@RequestBody OrderStatusRequestDto request) {
        return ResponseEntity.ok(service.getStatusesWithFilters(request));
    }
}
