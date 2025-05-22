package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.File;
import org.springframework.http.HttpHeaders;

import jakarta.servlet.http.HttpServletResponse;
import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.response.OrderProfitDTO;
import vn.congdubai.shopping.domain.response.ResCategorySalesDTO;
import vn.congdubai.shopping.domain.response.TopUserStatisticDTO;
import vn.congdubai.shopping.service.DashBoardService;
import vn.congdubai.shopping.service.Jobscheduler;
import vn.congdubai.shopping.util.annotation.ApiMessage;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class DashBoardController {

    private final Jobscheduler jobscheduler;
    private final DashBoardService dashBoardService;

    public DashBoardController(DashBoardService dashBoardService, Jobscheduler jobscheduler) {
        this.dashBoardService = dashBoardService;
        this.jobscheduler = jobscheduler;
    }

    @GetMapping("/dashboard/count-user-by-day")
    @ApiMessage("Fetch quantity user")
    public ResponseEntity<Long> getCountUserByDay(@RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(this.dashBoardService.handleCountUserByDay(startDate, endDate));
    }

    @GetMapping("/dashboard/count-order-by-day")
    @ApiMessage("Fetch quantity order")
    public ResponseEntity<Long> getCountQuantityOrderByDay(@RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(this.dashBoardService.handleCountOrderByDay(startDate, endDate));
    }

    @GetMapping("/dashboard/count-cancel-order-by-day")
    @ApiMessage("Fetch quantity order")
    public ResponseEntity<Long> getCountQuantityCancelOrderByDay(@RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(this.dashBoardService.handleCountCancelOrderByDay(startDate, endDate));
    }

    @GetMapping("/dashboard/total-price-by-day")
    @ApiMessage("Fetch total price")
    public ResponseEntity<Double> getTotalPriceByDay(@RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(this.dashBoardService.handleGetTotalPriceByDay(startDate, endDate));
    }

    @GetMapping("/dashboard/currentOrder")
    @ApiMessage("Fetch current orders")
    public ResponseEntity<List<Order>> getCurrentOrder() {
        return ResponseEntity.ok(this.dashBoardService.handleFetchCurrrentOrderByDay());
    }

    @GetMapping("/dashboard/category-sale")
    @ApiMessage("Fetch category sale")
    public ResponseEntity<List<ResCategorySalesDTO>> getCategorySales(@RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(this.dashBoardService.handleFetchCategorySales(startDate, endDate));
    }

    @GetMapping("/dashboard/top-seller")
    @ApiMessage("Fetch top seller")
    public ResponseEntity<List<TopUserStatisticDTO>> getTopSeller(@RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(this.dashBoardService.handleFetchTopSellerByDay(startDate, endDate));
    }

    @GetMapping("/dashboard/revenue-by-channel")
    public ResponseEntity<List<Map<String, Object>>> getRevenueByChannel(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate) {

        List<Map<String, Object>> result = this.dashBoardService.getRevenueByChannel(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/dashboard/total-price")
    public ResponseEntity<Double> getRevenueByChannel(
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        return ResponseEntity.ok(this.dashBoardService.handleFetchTotalPriceByDay(startDate, endDate));
    }

    @GetMapping("/dashboard/order-profit")
    @ApiMessage("Fetch Order ProfitDTO")
    public ResponseEntity<List<OrderProfitDTO>> getOrderProfitDTO(@RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate) {
        return ResponseEntity.ok(this.dashBoardService.handleFetchOrderProfit(startDate, endDate));
    }

    @GetMapping("/dashboard/export-excel")
    public void exportExcel(
            @RequestParam("startDate") LocalDateTime startDate,
            @RequestParam("endDate") LocalDateTime endDate,
            HttpServletResponse response) {
        try {
            // Gọi phương thức xuất Excel
            jobscheduler.exportExcel(this.dashBoardService.handleFetchOrderProfit(startDate, endDate));

            // Đọc file Excel đã tạo
            File file = new File("temp.xlsx");
            try (FileInputStream fileInputStream = new FileInputStream(file);
                    OutputStream outputStream = response.getOutputStream()) {

                response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders_profit_report.xlsx");

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                file.delete(); // đảm bảo xóa file dù có lỗi hay không
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
