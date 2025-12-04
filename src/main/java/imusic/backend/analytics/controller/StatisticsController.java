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
    public ResponseEntity<List<OrderStatusStatsDto>> getOrderStatusStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                statisticsService.getOrderStatusStats(startDate, endDate)
        );
    }

    @GetMapping("/top-clients")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<TopClientDto>> getTopClients(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "5") int limit
    ) {
        return ResponseEntity.ok(statisticsService.getTopClients(startDate, endDate, limit));
    }

    @GetMapping("/top-products")
    public ResponseEntity<List<TopProductDto>> getTopProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                statisticsService.getTopProducts(startDate, endDate, limit)
        );
    }

    @GetMapping("/inventory-movements")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<InventoryMovementDto>> getInventoryMovements(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(statisticsService.getInventoryMovements(startDate, endDate, limit));
    }

    @GetMapping("/low-stock")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<LowStockProductDto>> getLowStockProducts(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(statisticsService.getLowStockProducts(startDate, endDate));
    }

    @GetMapping("/manager-rating")
    public ResponseEntity<List<ManagerRatingDto>> getManagerRatings(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                statisticsService.getManagerRatings(startDate, endDate, limit)
        );
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
            @RequestParam(defaultValue = "month") String groupBy,
            @RequestParam(defaultValue = "10") int limit
    ) {
        if (managerId != null)
            return ResponseEntity.ok(
                    statisticsService.getManagerProductSeasonality(managerId, productId, startDate, endDate, groupBy, limit)
            );

        return ResponseEntity.ok(
                statisticsService.getProductSeasonality(productId, startDate, endDate, groupBy, limit)
        );
    }

    @GetMapping("/category-sales")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<CategorySalesDto>> getCategorySales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) Long managerId
    ) {
        if (managerId != null)
            return ResponseEntity.ok(statisticsService.getManagerCategorySales(managerId, startDate, endDate, limit));
        else
            return ResponseEntity.ok(statisticsService.getCategorySales(startDate, endDate, limit));
    }

    @GetMapping("/manager/{managerId}/sales-trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<SalesTrendDto>> getManagerSalesTrends(
            @PathVariable Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "month") String groupBy
    ) {
        return ResponseEntity.ok(
                statisticsService.getManagerSalesTrend(managerId, startDate, endDate, groupBy, limit)
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

    @GetMapping("/inventory-movement-trends")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<InventoryMovementTrendDto>> getInventoryMovementTrends(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "month") String groupBy,
            @RequestParam(defaultValue = "200") int limit
    ) {
        return ResponseEntity.ok(
                statisticsService.getInventoryMovementTrends(
                        productId, categoryId, startDate, endDate, groupBy, limit
                )
        );
    }

    @GetMapping("/inventory-movement-details")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<InventoryMovementDetailDto>> getInventoryMovementDetails(
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "500") int limit
    ) {
        return ResponseEntity.ok(
                statisticsService.getInventoryMovementDetails(
                        productId, categoryId, startDate, endDate, limit
                )
        );
    }

    @GetMapping("/avg-check")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<AvgCheckDto>> getAvgChecks(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                statisticsService.getAvgChecks(startDate, endDate, limit)
        );
    }

    @GetMapping("/manager/{managerId}/avg-check")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<List<AvgCheckDto>> getManagerAvgChecks(
            @PathVariable Long managerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return ResponseEntity.ok(
                statisticsService.getManagerAvgChecks(managerId, startDate, endDate, limit)
        );
    }

    @GetMapping("/avg-check/{clientId}/details")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<AvgCheckDetailsResponse> getAvgCheckDetails(
            @PathVariable Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return ResponseEntity.ok(
                statisticsService.getAvgCheckDetails(clientId, startDate, endDate)
        );
    }
}
