package imusic.backend.controller.ref;

import imusic.backend.dto.request.ref.InventoryMovementTypeRequestDto;
import imusic.backend.dto.response.ref.InventoryMovementTypeResponseDto;
import imusic.backend.dto.create.ref.InventoryMovementTypeCreateDto;
import imusic.backend.dto.update.ref.InventoryMovementTypeUpdateDto;
import imusic.backend.service.ref.InventoryMovementTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ref/inventory-movement-types")
@RequiredArgsConstructor
public class InventoryMovementTypeController {

    private final InventoryMovementTypeService service;

    @GetMapping
    public ResponseEntity<List<InventoryMovementTypeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/filter")
    public ResponseEntity<List<InventoryMovementTypeResponseDto>> getWithFilters(@RequestBody InventoryMovementTypeRequestDto request) {
        return ResponseEntity.ok(service.getMovementsWithFilters(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryMovementTypeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryMovementTypeResponseDto> create(@RequestBody InventoryMovementTypeCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<InventoryMovementTypeResponseDto> update(@PathVariable Long id, @RequestBody InventoryMovementTypeUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
