package imusic.backend.service.ops;

import imusic.backend.dto.response.ops.CartItemResponseDto;
import imusic.backend.dto.response.ops.CartResponseDto;

import java.util.List;

public interface CartService {
    CartResponseDto getByClientId(Long clientId);
    List<CartItemResponseDto> getCartItems(Long cartId);
    CartResponseDto createCart(Long clientId);
    CartItemResponseDto addItemToCart(Long cartId, Long productId, int quantity);
    CartItemResponseDto  updateCartItem(Long cartItemId, int newQuantity);
    void removeItemFromCart(Long cartItemId);
    void clearCart(Long cartId);
}
