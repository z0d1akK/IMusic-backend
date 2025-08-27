package imusic.backend.controller.ops;

import imusic.backend.dto.create.ops.CategoryAttributeCreateDto;
import imusic.backend.dto.response.ops.CategoryAttributeResponseDto;
import imusic.backend.dto.update.ops.CategoryAttributeUpdateDto;
import imusic.backend.service.ops.CategoryAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category-attributes")
@RequiredArgsConstructor
public class CategoryAttributeController {

    private final CategoryAttributeService service;

    @GetMapping
    public ResponseEntity<List<CategoryAttributeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryAttributeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<CategoryAttributeResponseDto>> getByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(service.getByCategoryId(categoryId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<CategoryAttributeResponseDto> create(@RequestBody CategoryAttributeCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<CategoryAttributeResponseDto> update(@PathVariable Long id,
                                                               @RequestBody CategoryAttributeUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
