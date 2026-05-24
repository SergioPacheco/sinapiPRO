package com.sinapipro.api.report;

import org.dhatim.fastexcel.Workbook;
import org.dhatim.fastexcel.Worksheet;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Exportação Excel streaming via FastExcel.
 */
@Service
public class ExcelExportService {

    public byte[] export(String sheetName, List<String> headers, List<Map<String, Object>> rows) {
        try (var os = new ByteArrayOutputStream();
             var wb = new Workbook(os, "SinapiPRO", "1.0")) {
            var ws = wb.newWorksheet(sheetName);

            // Headers
            for (int col = 0; col < headers.size(); col++) {
                ws.value(0, col, headers.get(col));
                ws.style(0, col).bold().set();
            }

            // Data rows
            for (int row = 0; row < rows.size(); row++) {
                var data = rows.get(row);
                for (int col = 0; col < headers.size(); col++) {
                    var value = data.get(headers.get(col));
                    setCellValue(ws, row + 1, col, value);
                }
            }

            wb.finish();
            return os.toByteArray();
        } catch (Exception e) {
            throw new ReportGenerationException("Failed to generate Excel", e);
        }
    }

    private void setCellValue(Worksheet ws, int row, int col, Object value) {
        if (value == null) return;
        if (value instanceof Number n) ws.value(row, col, n.doubleValue());
        else if (value instanceof BigDecimal bd) ws.value(row, col, bd.doubleValue());
        else ws.value(row, col, value.toString());
    }
}
