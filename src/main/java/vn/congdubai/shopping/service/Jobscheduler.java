package vn.congdubai.shopping.service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;
import vn.congdubai.shopping.domain.response.OrderProfitDTO;

@Component
public class Jobscheduler {
    public void exportExcel(List<OrderProfitDTO> orders) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders Profit Report");

        // Cấu hình độ rộng cột
        int[] columnWidths = { 7000, 5000, 3000, 5000, 5000, 5000, 5000 };
        for (int i = 0; i < columnWidths.length; i++) {
            sheet.setColumnWidth(i, columnWidths[i]);
        }

        // Tiêu đề
        Row titleRow = sheet.createRow(0);
        CellStyle titleStyle = workbook.createCellStyle();
        XSSFFont titleFont = ((XSSFWorkbook) workbook).createFont();
        titleFont.setFontHeightInPoints((short) 16);
        titleFont.setBold(true);
        titleStyle.setFont(titleFont);
        titleStyle.setAlignment(HorizontalAlignment.CENTER);

        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Báo cáo lợi nhuận đơn hàng");
        titleCell.setCellStyle(titleStyle);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 6));

        // Header
        String[] headers = { "Khách hàng", "Số điện thoại", "Số lượng", "Tổng tiền", "Giá nhập", "Lợi nhuận",
                "Ngày đặt" };
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        Row headerRow = sheet.createRow(1);
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Format tiền tệ và ngày tháng
        CreationHelper createHelper = workbook.getCreationHelper();

        CellStyle currencyStyle = workbook.createCellStyle();
        currencyStyle.setDataFormat(createHelper.createDataFormat().getFormat("#,##0 \"VNĐ\""));
        currencyStyle.setBorderBottom(BorderStyle.THIN);
        currencyStyle.setBorderTop(BorderStyle.THIN);
        currencyStyle.setBorderLeft(BorderStyle.THIN);
        currencyStyle.setBorderRight(BorderStyle.THIN);

        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("dd/MM/yyyy HH:mm"));
        dateStyle.setBorderBottom(BorderStyle.THIN);
        dateStyle.setBorderTop(BorderStyle.THIN);
        dateStyle.setBorderLeft(BorderStyle.THIN);
        dateStyle.setBorderRight(BorderStyle.THIN);

        int rowIndex = 2;
        double totalProfit = 0;

        for (OrderProfitDTO order : orders) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(order.getCustomerName());
            row.createCell(1).setCellValue(order.getPhone());
            row.createCell(2).setCellValue(order.getQuantity());

            Cell totalPriceCell = row.createCell(3);
            totalPriceCell.setCellValue(order.getTotalPrice());
            totalPriceCell.setCellStyle(currencyStyle);

            Cell minPriceCell = row.createCell(4);
            minPriceCell.setCellValue(order.getMinPrice());
            minPriceCell.setCellStyle(currencyStyle);

            Cell profitCell = row.createCell(5);
            profitCell.setCellValue(order.getProfit());
            profitCell.setCellStyle(currencyStyle);
            totalProfit += order.getProfit();

            Cell dateCell = row.createCell(6);
            if (order.getOrderDate() != null) {
                dateCell.setCellValue(order.getOrderDate());
            } else {
                dateCell.setCellValue("");
            }
            dateCell.setCellStyle(dateStyle);
        }

        // Dòng tổng lợi nhuận
        Row totalRow = sheet.createRow(rowIndex);
        Cell totalLabelCell = totalRow.createCell(0);
        totalLabelCell.setCellValue("Tổng lợi nhuận");
        totalLabelCell.setCellStyle(headerStyle);

        Cell totalProfitCell = totalRow.createCell(1);
        totalProfitCell.setCellValue(totalProfit);
        totalProfitCell.setCellStyle(currencyStyle);

        // Ghi ra file Excel
        String fileLocation = "temp.xlsx";
        try (FileOutputStream outputStream = new FileOutputStream(fileLocation)) {
            workbook.write(outputStream);
        }

        workbook.close();
    }
}
