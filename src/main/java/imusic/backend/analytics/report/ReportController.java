package imusic.backend.analytics.report;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/manager/{managerId}/sales")
    public ResponseEntity<byte[]> managerSalesReport(
            @PathVariable Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String groupBy,
            @RequestParam(defaultValue = "200") int limit
    ) {
        byte[] pdf = reportService.managerSalesReport(managerId, startDate, endDate, groupBy, limit);
        return build("manager_sales_report.pdf", pdf);
    }

    @GetMapping("/manager/{managerId}/top-clients")
    public ResponseEntity<byte[]> managerClientsReport(
            @PathVariable Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        byte[] pdf = reportService.managerTopClientsReport(managerId, startDate, endDate, limit);
        return build("manager_top_clients.pdf", pdf);
    }

    @GetMapping("/manager/{managerId}/top-products")
    public ResponseEntity<byte[]> managerProductsReport(
            @PathVariable Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        byte[] pdf = reportService.managerTopProductsReport(managerId, startDate, endDate, limit);
        return build("manager_top_products.pdf", pdf);
    }

    @GetMapping("/admin/sales")
    public ResponseEntity<byte[]> adminSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam String groupBy
    ) {
        byte[] pdf = reportService.adminSalesReport(startDate, endDate, groupBy);
        return build("admin_sales_report.pdf", pdf);
    }

    @GetMapping("/admin/top-managers")
    public ResponseEntity<byte[]> adminTopManagersReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "10") int limit
    ) {
        byte[] pdf = reportService.adminTopManagersReport(startDate, endDate, limit);
        return build("admin_top_managers.pdf", pdf);
    }

    @GetMapping("/admin/top-products")
    public ResponseEntity<byte[]> adminTopProductsReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(defaultValue = "100") int limit
    ) {
        byte[] pdf = reportService.adminTopProductsReport(startDate, endDate, limit);
        return build("admin_top_products.pdf", pdf);
    }

    private ResponseEntity<byte[]> build(String filename, byte[] bytes) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .body(bytes);
    }
}
