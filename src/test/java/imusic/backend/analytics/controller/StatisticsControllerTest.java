package imusic.backend.analytics.controller;

import imusic.backend.analytics.dto.*;
import imusic.backend.analytics.service.StatisticsService;
import imusic.backend.token.JwtService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService service;

    @MockBean
    private JwtService jwtService;

    @Test
    void testGetOverview() throws Exception {
        OverviewStatsDto dto = new OverviewStatsDto();
        dto.setTotalOrders(10);
        dto.setTotalRevenue(BigDecimal.valueOf(500));

        when(service.getOverviewStats(null, null)).thenReturn(dto);

        mockMvc.perform(get("/api/statistics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(10))
                .andExpect(jsonPath("$.totalRevenue").value(500));
    }

    @Test
    void testGetTopClients() throws Exception {
        TopClientDto dto = new TopClientDto();
        dto.setClientName("John");
        dto.setTotalSpent(BigDecimal.valueOf(300));

        when(service.getTopClients(null, null, 5)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/statistics/top-clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientName").value("John"))
                .andExpect(jsonPath("$[0].totalSpent").value(300));
    }

    @Test
    void testGetManagerTopProductsEndpoint() throws Exception {
        TopProductDto dto = new TopProductDto();
        dto.setProductName("Piano");
        dto.setProductId(10L);
        dto.setTotalSold(3);
        dto.setTotalRevenue(BigDecimal.valueOf(500));

        when(service.getManagerTopProducts(2L, null, null, 10)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/statistics/manager/2/top-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Piano"))
                .andExpect(jsonPath("$[0].productId").value(10));
    }
}
