package imusic.backend.analytics.controller;

import imusic.backend.analytics.dto.*;
import imusic.backend.analytics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @GetMapping("/overview")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<OverviewStatsDto> getOverviewStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(statisticsService.getOverviewStats(startDate, endDate));
    }

    @GetMapping("/sales-trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<SalesTrendDto>> getSalesTrends(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "month") String groupBy
    ) {
        return ResponseEntity.ok(statisticsService.getSalesTrends(startDate, endDate, groupBy));
    }

    @GetMapping("/order-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<OrderStatusStatsDto>> getOrderStatusStats() {
        return ResponseEntity.ok(statisticsService.getOrderStatusStats());
    }

    @GetMapping("/top-clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TopClientDto>> getTopClients(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(statisticsService.getTopClients(limit));
    }

    @GetMapping("/top-products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TopProductDto>> getTopProducts(
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(statisticsService.getTopProducts(limit));
    }

    @GetMapping("/inventory-movements")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<InventoryMovementDto>> getInventoryMovements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(statisticsService.getInventoryMovements(startDate, endDate));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<LowStockProductDto>> getLowStockProducts() {
        return ResponseEntity.ok(statisticsService.getLowStockProducts());
    }

    @GetMapping("/manager-rating")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ManagerRatingDto>> getManagerRatings() {
        return ResponseEntity.ok(statisticsService.getManagerRatings());
    }

    @GetMapping("/active-users")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<Long> getActiveUsersCount(@RequestParam(defaultValue = "30") int lastDays) {
        return ResponseEntity.ok(statisticsService.getActiveUsersCount(lastDays));
    }

    @GetMapping("/product/{productId}/seasonality")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<ProductSeasonalityDto>> getProductSeasonality(
            @PathVariable Long productId,
            @RequestParam(required = false) Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "month") String groupBy
    ) {
        if (managerId != null)
            return ResponseEntity.ok(
                    statisticsService.getManagerProductSeasonality(managerId, productId, startDate, endDate, groupBy)
            );

        return ResponseEntity.ok(
                statisticsService.getProductSeasonality(productId, startDate, endDate, groupBy)
        );
    }

    @GetMapping("/category-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<CategorySalesDto>> getCategorySales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long managerId
    ) {
        if (managerId != null)
            return ResponseEntity.ok(statisticsService.getManagerCategorySales(managerId, startDate, endDate));
        else
            return ResponseEntity.ok(statisticsService.getCategorySales(startDate, endDate));
    }

    @GetMapping("/manager/{managerId}/sales-trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<SalesTrendDto>> getManagerSalesTrends(
            @PathVariable Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "month") String groupBy
    ) {
        return ResponseEntity.ok(
                statisticsService.getManagerSalesTrend(managerId, startDate, endDate, groupBy)
        );
    }

    @GetMapping("/manager/{managerId}/top-clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TopClientDto>> getManagerTopClients(
            @PathVariable Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(statisticsService.getManagerTopClients(managerId, startDate, endDate, limit));
    }

    @GetMapping("/manager/{managerId}/top-products")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TopProductDto>> getManagerTopProducts(
            @PathVariable Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                statisticsService.getManagerTopProducts(managerId, startDate, endDate, limit)
        );
    }
}
