package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.OrderService;
import vn.congdubai.shopping.service.UserService;
import vn.congdubai.shopping.util.SecurityUtil;
import vn.congdubai.shopping.util.annotation.ApiMessage;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/history")
    @ApiMessage("Fetch all orders by user")
    public ResponseEntity<ResultPaginationDTO> fetchCategories(
            Pageable pageable) {
        Optional<String> optionalUsername = SecurityUtil.getCurrentUserLogin();
        User user = userService.handleGetUserByUsername(optionalUsername.get());
        return ResponseEntity.ok(this.orderService.handleFetchOrderByUser(user, pageable));
    }

}
