package imusic.backend.service.ops;

import imusic.backend.dto.response.ops.CartItemResponseDto;
import imusic.backend.dto.response.ops.CartResponseDto;
import imusic.backend.entity.ops.*;
import imusic.backend.mapper.ops.CartItemMapper;
import imusic.backend.mapper.ops.CartMapper;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.repository.ops.*;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.impl.ops.CartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceImplTest {

    @InjectMocks
    private CartServiceImpl service;

    @Mock private CartRepository cartRepository;
    @Mock private CartItemRepository cartItemRepository;
    @Mock private CartItemMapper cartItemMapper;
    @Mock private CartMapper cartMapper;
    @Mock private ClientRepository clientRepository;
    @Mock private UserMapper userMapper;
    @Mock private AuthService authService;
    @Mock private ProductRepository productRepository;
    @Mock private InventoryMovementService inventoryMovementService;
    @Mock private RoleResolver roleResolver;
    @Mock private UserStatusResolver userStatusResolver;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("getByClientId — возвращает существующую корзину")
    void testGetByClientIdExists() {
        Cart cart = new Cart();
        CartResponseDto dto = new CartResponseDto();
        when(cartRepository.findByClientId(1L)).thenReturn(Optional.of(cart));
        when(cartMapper.toResponse(cart)).thenReturn(dto);

        CartResponseDto result = service.getByClientId(1L);
        assertNotNull(result);
    }

    @Test
    @DisplayName("getByClientId — создаёт новую корзину, если нет")
    void testGetByClientIdCreate() {
        Client client = new Client();
        client.setId(1L);

        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
        when(cartRepository.findByClientId(1L)).thenReturn(Optional.empty());

        Cart newCart = new Cart();
        CartResponseDto dto = new CartResponseDto();

        when(cartRepository.save(any())).thenReturn(newCart);
        when(cartMapper.toResponse(newCart)).thenReturn(dto);

        CartResponseDto result = service.getByClientId(1L);
        assertNotNull(result);
    }

    @Test
    @DisplayName("addItemToCart — добавляет новый товар")
    void testAddItemToCartNew() {
        Cart cart = new Cart();
        Client client = new Client();
        User user = new User();
        user.setId(1L);
        client.setUser(user);
        cart.setClient(client);
        Product product = new Product();
        product.setId(1L);
        product.setStockQuantity(10);
        product.setName("Test Product");

        CartItem item = new CartItem();
        CartItemResponseDto dto = new CartItemResponseDto();

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartItemRepository.findByCartIdAndProductId(1L,1L)).thenReturn(Optional.empty());
        when(cartItemRepository.save(any())).thenReturn(item);
        when(cartItemMapper.toResponse(item)).thenReturn(dto);

        CartItemResponseDto result = service.addItemToCart(1L,1L,2);
        assertNotNull(result);
        assertEquals(8, product.getStockQuantity());
        verify(inventoryMovementService).createInventoryMovement(product, -2, "RESERVE_IN_CART", "Товар зарезервирован в корзине пользователем: " + user.getUsername());
    }

    @Test
    @DisplayName("updateCartItem — увеличивает количество")
    void testUpdateCartItemIncrease() {
        Product product = new Product();
        product.setStockQuantity(10);
        CartItem item = new CartItem();
        item.setQuantity(2);
        item.setProduct(product);
        Cart cart = new Cart();
        Client client = new Client();
        User user = new User();
        user.setId(1L);
        client.setUser(user);
        cart.setClient(client);
        item.setCart(cart);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);
        when(cartItemRepository.save(any())).thenReturn(item);
        when(cartItemMapper.toResponse(item)).thenReturn(new CartItemResponseDto());

        service.updateCartItem(1L, 5);
        assertEquals(7, product.getStockQuantity());
        verify(inventoryMovementService).createInventoryMovement(product, -3, "RESERVE_IN_CART", "Увеличено количество в корзине пользователя " + user.getUsername());
    }

    @Test
    @DisplayName("removeItemFromCart — удаляет товар")
    void testRemoveItemFromCart() {
        Product product = new Product();
        product.setStockQuantity(5);
        CartItem item = new CartItem();
        item.setQuantity(2);
        item.setProduct(product);
        Cart cart = new Cart();
        Client client = new Client();
        User user = new User();
        user.setId(1L);
        client.setUser(user);
        cart.setClient(client);
        item.setCart(cart);

        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);

        service.removeItemFromCart(1L);
        assertEquals(7, product.getStockQuantity());
        verify(cartItemRepository).delete(item);
        verify(inventoryMovementService).createInventoryMovement(product, 2, "RETURN_TO_STOCK", "Удаление товара из корзины пользователя " + user.getUsername());
    }

    @Test
    @DisplayName("clearCart — очищает корзину")
    void testClearCart() {
        Product product = new Product();
        product.setStockQuantity(5);
        CartItem item1 = new CartItem();
        item1.setProduct(product);
        item1.setQuantity(2);
        CartItem item2 = new CartItem();
        item2.setProduct(product);
        item2.setQuantity(3);
        Cart cart = new Cart();
        Client client = new Client();
        User user = new User();
        user.setId(1L);
        client.setUser(user);
        cart.setClient(client);

        List<CartItem> items = List.of(item1, item2);

        when(cartRepository.findById(1L)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartId(1L)).thenReturn(items);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);

        service.clearCart(1L);

        assertEquals(10, product.getStockQuantity());
        verify(cartItemRepository).deleteAll(items);
        verify(inventoryMovementService, times(2)).createInventoryMovement(any(), anyInt(), anyString(), anyString());
    }
}
