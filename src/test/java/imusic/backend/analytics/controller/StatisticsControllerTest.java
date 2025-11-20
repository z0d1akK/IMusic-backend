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

    @Autowired private MockMvc mockMvc;

    @MockBean private JwtService jwtService;
    @MockBean private StatisticsService service;

    @Test
    void testOverviewStats() throws Exception {
        OverviewStatsDto dto = new OverviewStatsDto();
        dto.setTotalOrders(10);
        dto.setTotalRevenue(BigDecimal.valueOf(500));

        when(service.getOverviewStats(null, null)).thenReturn(dto);

        mockMvc.perform(get("/api/statistics/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOrders").value(10));
    }

    @Test
    void testTopProducts() throws Exception {
        TopProductDto dto = new TopProductDto();
        dto.setProductName("Guitar");
        dto.setTotalSold(5);
        dto.setTotalRevenue(BigDecimal.valueOf(200));

        when(service.getTopProducts(5)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/statistics/top-products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productName").value("Guitar"));
    }

    @Test
    void testTopClients() throws Exception {
        TopClientDto dto = new TopClientDto();
        dto.setClientName("John");
        dto.setTotalSpent(BigDecimal.valueOf(300));

        when(service.getTopClients(5)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/statistics/top-clients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].clientName").value("John"));
    }
}

