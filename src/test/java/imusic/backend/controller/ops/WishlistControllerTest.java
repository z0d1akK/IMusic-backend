package imusic.backend.controller.ops;

import imusic.backend.service.ops.WishlistService;
import imusic.backend.token.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WishlistController.class)
class WishlistControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private WishlistService wishlistService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    @DisplayName("GET /api/wishlist — возвращает список")
    @WithMockUser
    void testGetWishlist() throws Exception {
        when(wishlistService.getUserWishlist()).thenReturn(List.of());

        mvc.perform(get("/api/wishlist"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/wishlist/{id} — добавляет товар")
    @WithMockUser
    void testAddProduct() throws Exception {
        mvc.perform(post("/api/wishlist/5")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(wishlistService).addProduct(5L);
    }

    @Test
    @DisplayName("DELETE /api/wishlist/{id} — удаляет товар")
    @WithMockUser
    void testRemoveProduct() throws Exception {
        mvc.perform(delete("/api/wishlist/5")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(wishlistService).removeProduct(5L);
    }
}