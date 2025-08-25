package imusic.backend.controller.ref;

import imusic.backend.dto.request.ref.PaymentMethodRequestDto;
import imusic.backend.dto.response.ref.PaymentMethodResponseDto;
import imusic.backend.dto.create.ref.PaymentMethodCreateDto;
import imusic.backend.dto.update.ref.PaymentMethodUpdateDto;
import imusic.backend.service.ref.PaymentMethodService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ref/payment-methods")
@RequiredArgsConstructor
public class PaymentMethodController {

    private final PaymentMethodService service;

    @GetMapping
    public ResponseEntity<List<PaymentMethodResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentMethodResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentMethodResponseDto> create(@RequestBody PaymentMethodCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentMethodResponseDto> update(@PathVariable Long id, @RequestBody PaymentMethodUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<PaymentMethodResponseDto>> getWithFilters(@RequestBody PaymentMethodRequestDto request) {
        return ResponseEntity.ok(service.getMethodsWithFilters(request));
    }
}
