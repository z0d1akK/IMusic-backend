package imusic.backend.controller.ops;

import imusic.backend.dto.create.ops.InventoryMovementCreateDto;
import imusic.backend.dto.request.ops.InventoryMovementRequestDto;
import imusic.backend.dto.response.ops.InventoryMovementResponseDto;
import imusic.backend.dto.update.ops.InventoryMovementUpdateDto;
import imusic.backend.service.ops.InventoryMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/inventory-movements")
@RequiredArgsConstructor
public class InventoryMovementController {

    private final InventoryMovementService movementService;

    @GetMapping
    public ResponseEntity<List<InventoryMovementResponseDto>> getAll() {
        return ResponseEntity.ok(movementService.getAll());
    }

    @PostMapping("/paged")
    public ResponseEntity<List<InventoryMovementResponseDto>> filterProducts(@RequestBody InventoryMovementRequestDto request) {
        return ResponseEntity.ok(movementService.getMovementsWithFilters(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryMovementResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(movementService.getById(id));
    }


    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryMovementResponseDto> create(@RequestBody InventoryMovementCreateDto dto) {
        return ResponseEntity.status(201).body(movementService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<InventoryMovementResponseDto> update(
            @PathVariable Long id,
            @RequestBody InventoryMovementUpdateDto dto
    ) {
        return ResponseEntity.ok(movementService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        movementService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
