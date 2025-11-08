package imusic.backend.controller.ops;

import imusic.backend.dto.create.ops.ProductCreateDto;
import imusic.backend.dto.request.ops.ProductRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.CategoryAttributeResponseDto;
import imusic.backend.dto.response.ops.ProductAttributeResponseDto;
import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.dto.update.ops.ProductUpdateDto;
import imusic.backend.service.ops.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAllProducts() {
        return ResponseEntity.ok(productService.getAll());
    }

    @PostMapping("/paged")
    public ResponseEntity<PageResponseDto<ProductResponseDto>> getPagedProducts(@RequestBody ProductRequestDto request) {
        return ResponseEntity.ok(productService.getPagedProducts(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductResponseDto> createProduct(@RequestBody ProductCreateDto dto) {
        return ResponseEntity.ok(productService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<ProductResponseDto> updateProduct(@PathVariable Long id,
                                                            @RequestBody ProductUpdateDto dto) {
        return ResponseEntity.ok(productService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/image")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<Void> uploadProductImage(@PathVariable Long id,
                                                   @RequestParam("image") MultipartFile imageFile) {
        productService.uploadProductImage(id, imageFile);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/attributes")
    public ResponseEntity<List<ProductAttributeResponseDto>> getProductAttributes(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getAttributesByProductId(id));
    }

    @GetMapping("/{id}/attributes-with-values")
    public ResponseEntity<List<CategoryAttributeResponseDto>> getProductAttributesWithValues(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getCategoryAttributesWithValues(id));
    }
}
