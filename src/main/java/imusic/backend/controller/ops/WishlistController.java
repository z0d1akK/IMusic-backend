package imusic.backend.controller.ops;

import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.service.ops.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public List<ProductResponseDto> get() {
        return wishlistService.getUserWishlist();
    }

    @PostMapping("/{productId}")
    public void add(@PathVariable Long productId) {
        wishlistService.addProduct(productId);
    }

    @DeleteMapping("/{productId}")
    public void remove(@PathVariable Long productId) {
        wishlistService.removeProduct(productId);
    }
}