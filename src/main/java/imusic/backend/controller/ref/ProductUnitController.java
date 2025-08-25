package imusic.backend.controller.ref;

import imusic.backend.dto.request.ref.ProductUnitRequestDto;
import imusic.backend.dto.response.ref.ProductUnitResponseDto;
import imusic.backend.dto.create.ref.ProductUnitCreateDto;
import imusic.backend.dto.update.ref.ProductUnitUpdateDto;
import imusic.backend.service.ref.ProductUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ref/product-units")
@RequiredArgsConstructor
public class ProductUnitController {

    private final ProductUnitService service;

    @GetMapping
    public ResponseEntity<List<ProductUnitResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductUnitResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductUnitResponseDto> create(@RequestBody ProductUnitCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductUnitResponseDto> update(@PathVariable Long id, @RequestBody ProductUnitUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<ProductUnitResponseDto>> getWithFilters(@RequestBody ProductUnitRequestDto request) {
        return ResponseEntity.ok(service.getUnitsWithFilters(request));
    }
}
