package imusic.backend.controller.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.response.ops.CartItemResponseDto;
import imusic.backend.dto.response.ops.CartResponseDto;
import imusic.backend.service.ops.CartService;
import imusic.backend.token.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
class CartControllerTest {

    @Autowired private MockMvc mvc;
    @Autowired private ObjectMapper mapper;
    @MockBean private JwtService jwtService;
    @MockBean private CartService cartService;

    @Test
    @DisplayName("GET /api/cart/{clientId} — возвращает корзину")
    @WithMockUser(roles = "CLIENT")
    void testGetCart() throws Exception {
        CartResponseDto dto = new CartResponseDto();
        dto.setId(1L);

        when(cartService.getByClientId(1L)).thenReturn(dto);

        mvc.perform(get("/api/cart/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("POST /api/cart/{clientId} — создаёт корзину")
    @WithMockUser(roles = "ADMIN")
    void testCreateCart() throws Exception {
        CartResponseDto dto = new CartResponseDto();
        dto.setId(2L);

        when(cartService.createCart(2L)).thenReturn(dto);

        mvc.perform(post("/api/cart/2").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2L));
    }

    @Test
    @DisplayName("GET /api/cart/{cartId}/items — возвращает товары")
    @WithMockUser(roles = "CLIENT")
    void testGetCartItems() throws Exception {
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(1L);

        when(cartService.getCartItems(1L)).thenReturn(List.of(dto));

        mvc.perform(get("/api/cart/1/items"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("POST /api/cart/{cartId}/items — добавляет товар")
    @WithMockUser(roles = "CLIENT")
    void testAddItem() throws Exception {
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(1L);

        when(cartService.addItemToCart(1L,1L,2)).thenReturn(dto);

        mvc.perform(post("/api/cart/1/items")
                        .with(csrf())
                        .param("productId", "1")
                        .param("quantity", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PUT /api/cart/items/{itemId} — обновляет товар")
    @WithMockUser(roles = "CLIENT")
    void testUpdateItem() throws Exception {
        CartItemResponseDto dto = new CartItemResponseDto();
        dto.setId(5L);

        when(cartService.updateCartItem(5L, 3)).thenReturn(dto);

        mvc.perform(put("/api/cart/items/5")
                        .with(csrf())
                        .param("quantity", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    @DisplayName("DELETE /api/cart/items/{itemId} — удаляет товар")
    @WithMockUser(roles = "CLIENT")
    void testRemoveItem() throws Exception {
        doNothing().when(cartService).removeItemFromCart(1L);

        mvc.perform(delete("/api/cart/items/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(cartService).removeItemFromCart(1L);
    }

    @Test
    @DisplayName("DELETE /api/cart/{cartId}/items — очищает корзину")
    @WithMockUser(roles = "CLIENT")
    void testClearCart() throws Exception {
        doNothing().when(cartService).clearCart(1L);

        mvc.perform(delete("/api/cart/1/items").with(csrf()))
                .andExpect(status().isNoContent());

        verify(cartService).clearCart(1L);
    }
}
