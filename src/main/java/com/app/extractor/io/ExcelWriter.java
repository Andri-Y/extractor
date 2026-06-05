package com.app.extractor.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Клас для експорту зібраних даних у формат Excel (.xlsx).
 * Реалізовано з урахуванням архітектурних вимог Gr1m.
 */
public class ExcelWriter {

    /**
     * Метод: save
     * Реалізація: Створює книгу Excel, заповнює її даними та зберігає на диск.
     * @param path Шлях до вихідного файлу.
     * @param headers Список заголовків стовпців.
     * @param rows Список рядків (кожен рядок - це список значень клітинок).
     */
    public void save(String path, List<String> headers, List<List<String>> rows) throws IOException {
        // 1. Створюємо нову книгу Excel (.xlsx)
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Результати екстракції");

            // 2. Створюємо рядок заголовків (Header Row)
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = createHeaderStyle(workbook);

            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // 3. Заповнюємо таблицю даними
            for (int i = 0; i < rows.size(); i++) {
                Row row = sheet.createRow(i + 1); // Починаємо з другого рядка
                List<String> rowData = rows.get(i);
                
                for (int j = 0; j < rowData.size(); j++) {
                    row.createCell(j).setCellValue(rowData.get(j));
                }
            }

            // 4. Автоматичне регулювання ширини стовпців для зручності
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            // 5. Запис файлу на диск
            try (FileOutputStream fileOut = new FileOutputStream(path)) {
                workbook.write(fileOut);
            }
        }
    }

    /**
     * Допоміжний метод для стилізації заголовків (Clean Code).
     */
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }
}