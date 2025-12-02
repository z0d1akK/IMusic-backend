package imusic.backend.analytics.report;

import imusic.backend.analytics.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final StatisticsService statisticsService;
    private final PdfReportGenerator pdf;

    public byte[] managerSalesReport(Long managerId, LocalDate start, LocalDate end, String groupBy) {
        var data = statisticsService.getManagerSalesTrend(managerId, start, end, groupBy);
        return pdf.salesTrendReport(data, "Отчёт по продажам менеджера");
    }

    public byte[] managerTopClientsReport(Long managerId, LocalDate start, LocalDate end) {
        var data = statisticsService.getManagerTopClients(managerId, start, end, 10);
        return pdf.topClientsReport(data, "Топ клиентов менеджера");
    }

    public byte[] managerTopProductsReport(Long managerId, LocalDate start, LocalDate end) {
        var data = statisticsService.getManagerTopProducts(managerId, start, end, 10);
        return pdf.topProductsReport(data, "Топ продуктов менеджера");
    }

    public byte[] adminSalesReport(LocalDate start, LocalDate end, String groupBy) {
        var data = statisticsService.getSalesTrends(start, end, groupBy);
        return pdf.adminSalesReport(data, "Общий отчёт по продажам компании");
    }

    public byte[] adminTopManagersReport() {
        var data = statisticsService.getManagerRatings();
        return pdf.managerRatingsReport(data, "Топ менеджеров по продажам");
    }

    public byte[] adminTopProductsReport() {
        var data = statisticsService.getTopProducts(10);
        return pdf.adminTopProductsReport(data, "Топ продуктов компании");
    }
}
