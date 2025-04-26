package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.DashBoardService;
import vn.congdubai.shopping.util.annotation.ApiMessage;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
    public ResponseEntity<Long> getCountUserByDay() {
        return ResponseEntity.ok(this.dashBoardService.handleCountUserByDay());
    }

    @GetMapping("/dashboard/count-order-by-day")
    @ApiMessage("Fetch quantity order")
    public ResponseEntity<Long> getCountQuantityOrderByDay() {
        return ResponseEntity.ok(this.dashBoardService.handleCountOrderByDay());
    }

    @GetMapping("/dashboard/total-price-by-day")
    @ApiMessage("Fetch total price")
    public ResponseEntity<Double> getTotalPriceByDay() {
        return ResponseEntity.ok(this.dashBoardService.handleGetTotalPriceByDay());
    }

    @GetMapping("/dashboard/currentOrder")
    @ApiMessage("Fetch current orders")
    public ResponseEntity<List<Order>> getCurrentOrder() {
        return ResponseEntity.ok(this.dashBoardService.handleFetchCurrrentOrderByDay());
    }

}
