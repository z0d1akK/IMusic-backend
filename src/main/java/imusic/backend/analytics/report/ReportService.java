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

    public byte[] managerSalesReport(Long managerId, LocalDate start, LocalDate end, String groupBy, int limit) {
        var data = statisticsService.getManagerSalesTrend(managerId, start, end, groupBy, limit);
        return pdf.salesTrendReport(data, "Отчёт по продажам менеджера");
    }

    public byte[] managerTopClientsReport(Long managerId, LocalDate start, LocalDate end, int limit) {
        var data = statisticsService.getManagerTopClients(managerId, start, end, limit);
        return pdf.topClientsReport(data, "Топ клиентов менеджера");
    }

    public byte[] managerTopProductsReport(Long managerId, LocalDate start, LocalDate end, int limit) {
        var data = statisticsService.getManagerTopProducts(managerId, start, end, limit);
        return pdf.topProductsReport(data, "Топ продуктов менеджера");
    }

    public byte[] adminSalesReport(LocalDate start, LocalDate end, String groupBy) {
        var data = statisticsService.getSalesTrends(start, end, groupBy);
        return pdf.adminSalesReport(data, "Общий отчёт по продажам компании");
    }

    public byte[] adminTopManagersReport(LocalDate start, LocalDate end, int limit) {
        var data = statisticsService.getManagerRatings(start, end, limit);
        return pdf.managerRatingsReport(data, "Топ менеджеров по продажам");
    }

    public byte[] adminTopProductsReport(LocalDate start, LocalDate end, int limit) {
        var data = statisticsService.getTopProducts(start, end, limit);
        return pdf.adminTopProductsReport(data, "Топ продуктов компании");
    }
}
