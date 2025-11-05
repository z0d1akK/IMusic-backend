package imusic.backend.controller.ops;

import imusic.backend.dto.response.ops.CartItemResponseDto;
import imusic.backend.dto.response.ops.CartResponseDto;
import imusic.backend.service.ops.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/{clientId}")
    @PreAuthorize("hasRole('CLIENT') or hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CartResponseDto> getCart(@PathVariable Long clientId) {
        return ResponseEntity.ok(cartService.getByClientId(clientId));
    }

    @PostMapping("/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CartResponseDto> createCart(@PathVariable Long clientId) {
        return ResponseEntity.ok(cartService.createCart(clientId));
    }

    @GetMapping("/{cartId}/items")
    @PreAuthorize("hasRole('CLIENT') or hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<CartItemResponseDto>> getCartItems(@PathVariable Long cartId) {
        return ResponseEntity.ok(cartService.getCartItems(cartId));
    }

    @PostMapping("/{cartId}/items")
    @PreAuthorize("hasRole('CLIENT') or hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CartItemResponseDto> addItem(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") int quantity) {
        return ResponseEntity.ok(cartService.addItemToCart(cartId, productId, quantity));
    }

    @PutMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CLIENT') or hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<CartItemResponseDto> updateItem(
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(itemId, quantity));
    }

    @DeleteMapping("/items/{itemId}")
    @PreAuthorize("hasRole('CLIENT') or hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> removeItem(@PathVariable Long itemId) {
        cartService.removeItemFromCart(itemId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    @PreAuthorize("hasRole('CLIENT') or hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Void> clearCart(@PathVariable Long cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

}
