package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.response.ResCategorySalesDTO;
import vn.congdubai.shopping.domain.response.ResSaleChannelSummaryDTO;
import vn.congdubai.shopping.domain.response.TopUserStatisticDTO;
import vn.congdubai.shopping.service.DashBoardService;
import vn.congdubai.shopping.util.annotation.ApiMessage;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/v1")
public class DashBoardController {
    private final DashBoardService dashBoardService;

    public DashBoardController(DashBoardService dashBoardService) {
        this.dashBoardService = dashBoardService;
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
}
