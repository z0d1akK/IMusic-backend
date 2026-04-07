package imusic.backend.service.ops;

import imusic.backend.entity.ops.Product;
import imusic.backend.entity.ops.User;
import imusic.backend.exception.AppException;
import imusic.backend.mapper.ops.ProductMapper;
import imusic.backend.mapper.ops.UserMapper;
import imusic.backend.mapper.resolver.ref.RoleResolver;
import imusic.backend.mapper.resolver.ref.UserStatusResolver;
import imusic.backend.repository.ops.ProductRepository;
import imusic.backend.repository.ops.WishlistItemRepository;
import imusic.backend.service.auth.AuthService;
import imusic.backend.service.impl.ops.WishlistServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class WishlistServiceImplTest {

    @Mock
    private WishlistItemRepository wishlistRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductMapper productMapper;
    @Mock
    private AuthService authService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleResolver roleResolver;
    @Mock
    private UserStatusResolver userStatusResolver;

    @InjectMocks
    private WishlistServiceImpl service;

    public WishlistServiceImplTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("addProduct — успешно добавляет товар")
    void testAddProductSuccess() {
        User user = new User();
        user.setId(1L);

        Product product = new Product();
        product.setId(5L);

        when(authService.getCurrentUser()).thenReturn(null);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);
        when(wishlistRepository.existsByUser_IdAndProduct_Id(1L, 5L)).thenReturn(false);
        when(productRepository.findById(5L)).thenReturn(Optional.of(product));

        service.addProduct(5L);

        verify(wishlistRepository).save(any());
    }

    @Test
    @DisplayName("addProduct — не добавляет дубликат")
    void testAddProductDuplicate() {
        User user = new User();
        user.setId(1L);

        when(authService.getCurrentUser()).thenReturn(null);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);
        when(wishlistRepository.existsByUser_IdAndProduct_Id(1L, 5L)).thenReturn(true);

        service.addProduct(5L);

        verify(wishlistRepository, never()).save(any());
    }

    @Test
    @DisplayName("addProduct — ошибка если товар не найден")
    void testAddProductNotFound() {
        User user = new User();
        user.setId(1L);

        when(authService.getCurrentUser()).thenReturn(null);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);
        when(wishlistRepository.existsByUser_IdAndProduct_Id(1L, 5L)).thenReturn(false);
        when(productRepository.findById(5L)).thenReturn(Optional.empty());

        assertThrows(AppException.class, () -> service.addProduct(5L));
    }

    @Test
    @DisplayName("removeProduct — удаляет товар")
    void testRemoveProduct() {
        User user = new User();
        user.setId(1L);

        when(authService.getCurrentUser()).thenReturn(null);
        when(userMapper.responseToEntity(any(), any(), any())).thenReturn(user);

        service.removeProduct(5L);

        verify(wishlistRepository).deleteByUser_IdAndProduct_Id(1L, 5L);
    }
}
