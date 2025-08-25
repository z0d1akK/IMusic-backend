package imusic.backend.controller.ref;

import imusic.backend.dto.request.ref.PaymentStatusRequestDto;
import imusic.backend.dto.response.ref.PaymentStatusResponseDto;
import imusic.backend.dto.create.ref.PaymentStatusCreateDto;
import imusic.backend.dto.update.ref.PaymentStatusUpdateDto;
import imusic.backend.service.ref.PaymentStatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ref/payment-statuses")
@RequiredArgsConstructor
public class PaymentStatusController {

    private final PaymentStatusService service;

    @GetMapping
    public ResponseEntity<List<PaymentStatusResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentStatusResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentStatusResponseDto> create(@RequestBody PaymentStatusCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentStatusResponseDto> update(@PathVariable Long id, @RequestBody PaymentStatusUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<PaymentStatusResponseDto>> getWithFilters(@RequestBody PaymentStatusRequestDto request) {
        return ResponseEntity.ok(service.getStatusesWithFilters(request));
    }
}
