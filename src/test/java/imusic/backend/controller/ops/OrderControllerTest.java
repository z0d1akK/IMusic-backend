package imusic.backend.controller.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.create.ops.OrderCreateDto;
import imusic.backend.dto.request.ops.OrderRequestDto;
import imusic.backend.dto.response.common.PageResponseDto;
import imusic.backend.dto.response.ops.OrderResponseDto;
import imusic.backend.dto.update.ops.OrderUpdateDto;
import imusic.backend.service.ops.OrderService;
import imusic.backend.token.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private OrderService orderService;

    @MockBean
    private JwtService jwtService;

    @Test
    @DisplayName("GET /api/orders/{id} — CLIENT/ADMIN/MANAGER могут получить заказ")
    @WithMockUser(roles = "CLIENT")
    void testGetOrderById() throws Exception {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(5L);

        when(orderService.getById(5L)).thenReturn(dto);

        mvc.perform(get("/api/orders/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L));
    }

    @Test
    @DisplayName("GET /api/orders/by-client — возвращает заказы по клиенту")
    @WithMockUser(roles = "CLIENT")
    void testGetOrdersByClient() throws Exception {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(1L);

        when(orderService.getOrdersByClientId(7L)).thenReturn(List.of(dto));

        mvc.perform(get("/api/orders/by-client")
                        .param("clientId", "7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("GET /api/orders/all — ADMIN/MANAGER получают все заказы")
    @WithMockUser(roles = "ADMIN")
    void testGetAllOrders() throws Exception {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(1L);

        when(orderService.getAll()).thenReturn(List.of(dto));

        mvc.perform(get("/api/orders/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("POST /api/orders — CLIENT/ADMIN/MANAGER создают заказ")
    @WithMockUser(roles = "CLIENT")
    void testCreateOrder() throws Exception {
        OrderCreateDto createDto = new OrderCreateDto();
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(99L);

        when(orderService.create(any())).thenReturn(dto);

        mvc.perform(post("/api/orders")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(99L));
    }

    @Test
    @DisplayName("PUT /api/orders/{id} — ADMIN/MANAGER обновляют заказ")
    @WithMockUser(roles = "MANAGER")
    void testUpdateOrder() throws Exception {
        OrderUpdateDto updateDto = new OrderUpdateDto();
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(10L);

        when(orderService.update(eq(10L), any())).thenReturn(dto);

        mvc.perform(put("/api/orders/10")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10L));
    }

    @Test
    @DisplayName("DELETE /api/orders/{id} — ADMIN/MANAGER удаляют заказ")
    @WithMockUser(roles = "ADMIN")
    void testDeleteOrder() throws Exception {
        doNothing().when(orderService).delete(5L);

        mvc.perform(delete("/api/orders/5")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(orderService, times(1)).delete(5L);
    }

    @Test
    @DisplayName("POST /api/orders/paged — ADMIN/MANAGER получают страницы заказов")
    @WithMockUser(roles = "MANAGER")
    void testGetPagedOrders() throws Exception {
        OrderRequestDto req = new OrderRequestDto();
        PageResponseDto<OrderResponseDto> page = new PageResponseDto<>(List.of(), 0, 12, 0, 0);

        when(orderService.getPagedOrders(any())).thenReturn(page);

        mvc.perform(post("/api/orders/paged")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
