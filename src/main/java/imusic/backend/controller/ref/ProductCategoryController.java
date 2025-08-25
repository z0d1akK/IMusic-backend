package imusic.backend.controller.ref;

import imusic.backend.dto.request.ref.ProductCategoryRequestDto;
import imusic.backend.dto.response.ref.ProductCategoryResponseDto;
import imusic.backend.dto.create.ref.ProductCategoryCreateDto;
import imusic.backend.dto.update.ref.ProductCategoryUpdateDto;
import imusic.backend.service.ref.ProductCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ref/product-categories")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final ProductCategoryService service;

    @GetMapping
    public ResponseEntity<List<ProductCategoryResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductCategoryResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCategoryResponseDto> create(@RequestBody ProductCategoryCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductCategoryResponseDto> update(@PathVariable Long id, @RequestBody ProductCategoryUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/filter")
    public ResponseEntity<List<ProductCategoryResponseDto>> getWithFilters(@RequestBody ProductCategoryRequestDto request) {
        return ResponseEntity.ok(service.getCategoriesWithFilters(request));
    }
}
