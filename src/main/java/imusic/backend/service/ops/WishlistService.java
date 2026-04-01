package imusic.backend.service.ops;

import imusic.backend.dto.response.ops.ProductResponseDto;

import java.util.List;

public interface WishlistService {
    void addProduct(Long productId);
    void removeProduct(Long productId);
    List<ProductResponseDto> getUserWishlist();
}
