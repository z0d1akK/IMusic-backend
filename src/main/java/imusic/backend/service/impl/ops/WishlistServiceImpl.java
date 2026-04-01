package imusic.backend.service.impl.ops;

import imusic.backend.dto.response.ops.ProductResponseDto;
import imusic.backend.entity.ops.Product;
import imusic.backend.entity.ops.User;
import imusic.backend.entity.ops.WishlistItem;
import imusic.backend.exception.AppException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import imusic.backend.repository.ops.WishlistItemRepository;
import imusic.backend.repository.ops.ProductRepository;
import imusic.backend.mapper.ops.ProductMapper;
import imusic.backend.service.ops.WishlistService;
import imusic.backend.service.auth.AuthService;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistItemRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final AuthService authService;
    private final UserMapper userMapper;
    private final RoleResolver roleResolver;
    private final UserStatusResolver userStatusResolver;

    @Override
    public void addProduct(Long productId) {
        User user = getCurrentUser();

        if (wishlistRepository.existsByUser_IdAndProduct_Id(user.getId(), productId)) {
            return;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Товар не найден"));

        WishlistItem item = WishlistItem.builder()
                .user(user)
                .product(product)
                .build();

        wishlistRepository.save(item);
    }

    @Override
    public void removeProduct(Long productId) {
        User user = getCurrentUser();
        wishlistRepository.deleteByUser_IdAndProduct_Id(user.getId(), productId);
    }

    @Override
    public List<ProductResponseDto> getUserWishlist() {
        User user = getCurrentUser();

        return wishlistRepository.findAllByUserId(user.getId())
                .stream()
                .map(WishlistItem::getProduct)
                .map(productMapper::toResponse)
                .toList();
    }

    private User getCurrentUser() {
        return userMapper.responseToEntity(
                authService.getCurrentUser(),
                roleResolver,
                userStatusResolver
        );
    }
}