package imusic.backend.controller.ops;

import imusic.backend.dto.create.ops.ProductAttributeCreateDto;
import imusic.backend.dto.request.ops.ProductAttributeRequestDto;
import imusic.backend.dto.request.ops.ProductRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.dto.update.ops.ProductAttributeUpdateDto;
import imusic.backend.service.ops.ProductAttributeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-attributes")
@RequiredArgsConstructor
public class ProductAttributeController {

    private final ProductAttributeService service;

    @GetMapping
    public ResponseEntity<List<ProductAttributeResponseDto>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @PostMapping("/paged")
    public ResponseEntity<PageResponseDto<ProductAttributeResponseDto>> getPagedAttributes(@RequestBody ProductAttributeRequestDto request) {
        return ResponseEntity.ok(service.getPagedAttributes(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductAttributeResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductAttributeResponseDto>> getByProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(service.getAllByProductId(productId));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductAttributeResponseDto> create(@RequestBody ProductAttributeCreateDto dto) {
        return ResponseEntity.ok(service.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductAttributeResponseDto> update(@PathVariable Long id,
                                                              @RequestBody ProductAttributeUpdateDto dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
