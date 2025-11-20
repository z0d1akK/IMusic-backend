package imusic.backend.service.impl.ops;

import imusic.backend.dto.response.ops.CartItemResponseDto;
import imusic.backend.dto.response.ops.CartResponseDto;
import imusic.backend.entity.ops.*;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.CartItemMapper;
import imusic.backend.mapper.ops.CartMapper;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.repository.ops.*;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.ops.CartService;
import imusic.backend.service.ops.InventoryMovementService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CartItemMapper cartItemMapper;
    private final CartMapper cartMapper;
    private final ClientRepository clientRepository;
    private final UserMapper userMapper;
    private final AuthService authService;
    private final ProductRepository productRepository;
    private final InventoryMovementService inventoryMovementService;
    private final RoleResolver roleResolver;
    private final UserStatusResolver userStatusResolver;

    @Override
    public CartResponseDto getByClientId(Long clientId) {
        return cartRepository.findByClientId(clientId)
                .map(cartMapper::toResponse)
                .orElseGet(() -> createCart(clientId));
    }

    @Override
    public List<CartItemResponseDto> getCartItems(Long cartId) {
        return cartItemRepository.findByCartId(cartId).stream()
                .map(cartItemMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public CartResponseDto createCart(Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new AppException("Клиент не найден, ключ: " + clientId));

        return cartRepository.findByClientId(clientId)
                .map(cartMapper::toResponse)
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .client(client)
                            .items(new ArrayList<>())
                            .build();
                    return cartMapper.toResponse(cartRepository.save(newCart));
                });
    }

    @Override
    public CartItemResponseDto addItemToCart(Long cartId, Long productId, int quantity) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new AppException("Корзина не найдена, ключ: " + cartId));

        User currentUser = userMapper.responseToEntity(authService.getCurrentUser(), roleResolver, userStatusResolver);
        if (cart.getClient() == null || !cart.getClient().getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Доступ запрещен, корзина не принадлежит текущему пользователю");
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new AppException("Продукт не найден, ключ: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new AppException("Недостаточно кол-ва в наличии для продукта: " + product.getName());
        }

        CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + quantity);
                    return existing;
                })
                .orElseGet(() -> CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(quantity)
                        .build());

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        inventoryMovementService.createInventoryMovement(product, -quantity, "RESERVE_IN_CART", "Товар зарезервирован в корзине пользователем: " + currentUser.getUsername());

        return cartItemMapper.toResponse(cartItemRepository.save(item));
    }

    @Override
    public CartItemResponseDto updateCartItem(Long cartItemId, int newQuantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Товар корзины не найден, ключ: " + cartItemId));

        User currentUser = userMapper.responseToEntity(authService.getCurrentUser(), roleResolver, userStatusResolver);
        if (item.getCart().getClient() == null || !item.getCart().getClient().getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Доступ запрещен, товар корзины не принадлежит текущему пользователю");
        }

        int diff = newQuantity - item.getQuantity();
        Product product = item.getProduct();

        if (diff > 0) {
            if (product.getStockQuantity() < diff) {
                throw new IllegalArgumentException("Недостаточно в наличии для увеличения количества");
            }
            product.setStockQuantity(product.getStockQuantity() - diff);
            inventoryMovementService.createInventoryMovement(product, -diff, "RESERVE_IN_CART", "Увеличено количество в корзине пользователя " + currentUser.getUsername());
        } else if (diff < 0) {
            product.setStockQuantity(product.getStockQuantity() + Math.abs(diff));
            inventoryMovementService.createInventoryMovement(product, Math.abs(diff), "RETURN_TO_STOCK", "Уменьшено количество в корзине пользователя " + currentUser.getUsername());
        }

        productRepository.save(product);
        item.setQuantity(newQuantity);
        return cartItemMapper.toResponse(cartItemRepository.save(item));
    }

    @Override
    public void removeItemFromCart(Long cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("Товар корзины не найден, ключ: " + cartItemId));

        User currentUser = userMapper.responseToEntity(authService.getCurrentUser(), roleResolver, userStatusResolver);
        if (item.getCart().getClient() == null || !item.getCart().getClient().getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Доступ запрещен, товар корзины не принадлежит текущему пользователю");
        }

        Product product = item.getProduct();
        int quantity = item.getQuantity();

            product.setStockQuantity(product.getStockQuantity() + quantity);
            productRepository.save(product);

            inventoryMovementService.createInventoryMovement(product, quantity, "RETURN_TO_STOCK", "Удаление товара из корзины пользователя " + currentUser.getUsername());

            cartItemRepository.delete(item);
    }

    @Override
    public void clearCart(Long cartId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new EntityNotFoundException("Корзина не найдена, ключ: " + cartId));

        User currentUser = userMapper.responseToEntity(authService.getCurrentUser(), roleResolver, userStatusResolver);
        if (cart.getClient() == null || !cart.getClient().getUser().getId().equals(currentUser.getId())) {
            throw new SecurityException("Доступ запрещен, корзина не принадлежит текущему пользователю");
        }

        List<CartItem> items = cartItemRepository.findByCartId(cartId);
        for (CartItem item : items) {
            Product product = item.getProduct();
            int quantity = item.getQuantity();

                product.setStockQuantity(product.getStockQuantity() + quantity);
                productRepository.save(product);

                inventoryMovementService.createInventoryMovement(product, quantity, "RETURN_TO_STOCK", "Очистка корзины пользователя " + currentUser.getUsername());
        }
        cartItemRepository.deleteAll(items);
    }
}
