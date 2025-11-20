package imusic.backend.controller.ops;

import com.fasterxml.jackson.databind.ObjectMapper;
import imusic.backend.dto.response.ops.OrderStatusHistoryResponseDto;
import imusic.backend.service.ops.OrderStatusHistoryService;
import imusic.backend.token.JwtService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderStatusHistoryController.class)
class OrderStatusHistoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderStatusHistoryService orderStatusHistoryService;

    @MockBean
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/order-status-history/{orderId} — получение истории заказа")
    @WithMockUser
    void testGetHistoryByOrderId() throws Exception {
        OrderStatusHistoryResponseDto dto = OrderStatusHistoryResponseDto.builder()
                .id(1L)
                .orderId(10L)
                .oldStatusName("OLD_STATUS")
                .changedAt(LocalDateTime.now())
                .build();

        when(orderStatusHistoryService.getHistoryByOrderId(10L))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/order-status-history/{orderId}", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].oldStatusName").value("OLD_STATUS"));
    }

    @Test
    @DisplayName("DELETE /api/order-status-history/{orderId} — удаление истории статусов")
    @WithMockUser(roles = {"ADMIN"})
    void testDeleteHistoryByOrderId() throws Exception {
        doNothing().when(orderStatusHistoryService).deleteHistoryByOrderId(10L);

        mockMvc.perform(delete("/api/order-status-history/{orderId}", 10L)
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(orderStatusHistoryService).deleteHistoryByOrderId(10L);
    }
}
