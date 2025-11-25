package imusic.backend.analytics.report;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import imusic.backend.analytics.dto.ManagerRatingDto;
import imusic.backend.analytics.dto.SalesTrendDto;
import imusic.backend.analytics.dto.TopClientDto;
import imusic.backend.analytics.dto.TopProductDto;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;

@Component
public class PdfReportGenerator {

    private final Font titleFont;
    private final Font textFont;
    private final Font headerFont;
    private final Font cellFont;

    public PdfReportGenerator() {

        try {
            var fontStream = getClass()
                    .getClassLoader()
                    .getResourceAsStream("fonts/arial.ttf");

            if (fontStream == null) {
                throw new RuntimeException("Файл fonts/arial.ttf не найден в resources");
            }

            byte[] fontBytes = fontStream.readAllBytes();

            BaseFont bf = BaseFont.createFont(
                    "arial.ttf",
                    BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED,
                    BaseFont.CACHED,
                    fontBytes,
                    null
            );

            titleFont = new Font(bf, 18, Font.BOLD, BaseColor.BLACK);
            textFont = new Font(bf, 12, Font.NORMAL, BaseColor.BLACK);
            headerFont = new Font(bf, 11, Font.BOLD, BaseColor.BLACK);
            cellFont = new Font(bf, 11, Font.NORMAL, BaseColor.BLACK);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка загрузки шрифта PDF", e);
        }
    }

    public byte[] salesTrendReport(List<SalesTrendDto> data, String title) {
        String intro =
                "Отчёт содержит анализ динамики продаж менеджера за выбранный период. " +
                        "Показатели сгруппированы по датам и позволяют оценить тенденции изменения выручки.";

        String bottom =
                "Данный отчёт используется для анализа сезонности, планирования активности менеджера " +
                        "и выявления периодов роста или снижения выручки.";

        String[] headers = {"Дата", "Выручка"};

        List<String[]> rows = data.stream()
                .map(d -> new String[]{d.getPeriod(), d.getTotalRevenue().toString()})
                .toList();

        return generatePdf(title, intro, headers, rows, bottom);
    }

    public byte[] topClientsReport(List<TopClientDto> data, String title) {
        String intro =
                "Отчёт содержит список ключевых клиентов менеджера, отсортированных по сумме покупок " +
                        "за выбранный период.";

        String bottom =
                "Информация используется для формирования индивидуальных предложений, удержания клиентов " +
                        "и выявления наиболее прибыльной аудитории.";

        String[] headers = {"Клиент", "Сумма покупок"};

        List<String[]> rows = data.stream()
                .map(d -> new String[]{d.getClientName(), d.getTotalSpent().toString()})
                .toList();

        return generatePdf(title, intro, headers, rows, bottom);
    }

    public byte[] topProductsReport(List<TopProductDto> data, String title) {
        String intro =
                "Отчёт показывает товары, которые принесли наибольшую выручку менеджеру за указанный период. " +
                        "Это позволяет оценить предпочтения клиентов и эффективность товарной матрицы.";

        String bottom =
                "Результаты используются для оптимизации ассортимента и корректировки стратегии продаж менеджера.";

        String[] headers = {"Продукт", "Продано", "Выручка"};

        List<String[]> rows = data.stream()
                .map(d -> new String[]{
                        d.getProductName(),
                        String.valueOf(d.getTotalSold()),
                        d.getTotalRevenue().toString()
                })
                .toList();

        return generatePdf(title, intro, headers, rows, bottom);
    }

    public byte[] adminSalesReport(List<SalesTrendDto> data, String title) {
        String intro =
                "Отчёт демонстрирует общую динамику продаж компании за выбранный период. " +
                        "Используется для стратегического анализа и оценки финансовых результатов.";

        String bottom =
                "Данные позволяют выявлять глобальные тенденции, прогнозировать оборот и принимать управленческие решения.";

        String[] headers = {"Дата", "Выручка"};

        List<String[]> rows = data.stream()
                .map(d -> new String[]{d.getPeriod(), d.getTotalRevenue().toString()})
                .toList();

        return generatePdf(title, intro, headers, rows, bottom);
    }

    public byte[] managerRatingsReport(List<ManagerRatingDto> data, String title) {
        String intro =
                "Отчёт формирует рейтинг менеджеров по количеству оформленных заказов и объёму выручки. " +
                        "Используется руководством для анализа эффективности сотрудников.";

        String bottom =
                "Рейтинг помогает определить лучших сотрудников, выделить зоны роста и вести аналитическую работу.";

        String[] headers = {"Менеджер", "Заказы", "Выручка"};

        List<String[]> rows = data.stream()
                .map(d -> new String[]{
                        d.getManagerName(),
                        String.valueOf(d.getTotalOrders()),
                        d.getTotalRevenue().toString()
                })
                .toList();

        return generatePdf(title, intro, headers, rows, bottom);
    }

    public byte[] adminTopProductsReport(List<TopProductDto> data, String title) {
        String intro =
                "Отчёт демонстрирует наиболее востребованные товары компании. " +
                        "Анализ основан на количестве продаж и общей выручке.";

        String bottom =
                "Отчёт используется для анализа ассортимента, оптимизации закупок и выявления прибыльных позиций.";

        String[] headers = {"Продукт", "Продано", "Выручка"};

        List<String[]> rows = data.stream()
                .map(d -> new String[]{
                        d.getProductName(),
                        String.valueOf(d.getTotalSold()),
                        d.getTotalRevenue().toString()
                })
                .toList();

        return generatePdf(title, intro, headers, rows, bottom);
    }

    private byte[] generatePdf(
            String title,
            String introText,
            String[] headers,
            List<String[]> rows,
            String bottomText
    ) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Document doc = new Document(PageSize.A4, 40, 40, 40, 40);
            PdfWriter.getInstance(doc, out);
            doc.open();

            Paragraph titleP = new Paragraph(title, titleFont);
            titleP.setAlignment(Element.ALIGN_CENTER);
            titleP.setSpacingAfter(20);
            doc.add(titleP);

            Paragraph intro = new Paragraph(introText, textFont);
            intro.setSpacingAfter(15);
            doc.add(intro);

            PdfPTable table = new PdfPTable(headers.length);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10);
            table.setSpacingAfter(15);

            Arrays.stream(headers).forEach(h -> {
                PdfPCell cell = new PdfPCell(new Phrase(h, headerFont));
                cell.setBackgroundColor(new BaseColor(240, 240, 240));
                cell.setPadding(6);
                table.addCell(cell);
            });

            rows.forEach(r -> Arrays.stream(r).forEach(v -> {
                PdfPCell cell = new PdfPCell(new Phrase(v, cellFont));
                cell.setPadding(5);
                table.addCell(cell);
            }));

            doc.add(table);

            Paragraph bottom = new Paragraph(bottomText, textFont);
            bottom.setSpacingBefore(10);
            doc.add(bottom);

            doc.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Ошибка формирования PDF", e);
        }
    }
}
