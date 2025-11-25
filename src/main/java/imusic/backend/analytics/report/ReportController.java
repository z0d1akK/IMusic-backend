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
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return build("manager_sales_report.pdf",
                reportService.managerSalesReport(managerId, startDate, endDate));
    }

    @GetMapping("/manager/{managerId}/top-clients")
    public ResponseEntity<byte[]> managerClientsReport(
            @PathVariable Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return build("manager_top_clients.pdf",
                reportService.managerTopClientsReport(managerId, startDate, endDate));
    }

    @GetMapping("/manager/{managerId}/top-products")
    public ResponseEntity<byte[]> managerProductsReport(
            @PathVariable Long managerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return build("manager_top_products.pdf",
                reportService.managerTopProductsReport(managerId, startDate, endDate));
    }

    @GetMapping("/admin/sales")
    public ResponseEntity<byte[]> adminSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        return build("admin_sales_report.pdf",
                reportService.adminSalesReport(startDate, endDate));
    }

    @GetMapping("/admin/top-managers")
    public ResponseEntity<byte[]> adminManagersReport() {
        return build("admin_top_managers.pdf",
                reportService.adminTopManagersReport());
    }

    @GetMapping("/admin/top-products")
    public ResponseEntity<byte[]> adminProductsReport() {
        return build("admin_top_products.pdf",
                reportService.adminTopProductsReport());
    }

    private ResponseEntity<byte[]> build(String filename, byte[] bytes) {
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + filename)
                .body(bytes);
    }
}
