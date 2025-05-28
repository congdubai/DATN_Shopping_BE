package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.congdubai.shopping.domain.Color;
import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.OrderDetail;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.ResProductSalesDTO;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.OrderRepository;
import vn.congdubai.shopping.service.OrderService;
import vn.congdubai.shopping.service.UserService;
import vn.congdubai.shopping.util.SecurityUtil;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/v1")
public class OrderController {
    private final OrderService orderService;
    private final UserService userService;
    private final OrderRepository orderRepository;

    public OrderController(OrderService orderService, UserService userService, OrderRepository orderRepository) {
        this.orderService = orderService;
        this.userService = userService;
        this.orderRepository = orderRepository;
    }

    // fetch history order
    @GetMapping("/history")
    @ApiMessage("Fetch all orders by user")
    public ResponseEntity<ResultPaginationDTO> fetchCategories(
            Pageable pageable) {
        Optional<String> optionalUsername = SecurityUtil.getCurrentUserLogin();
        User user = userService.handleGetUserByUsername(optionalUsername.get());
        return ResponseEntity.ok(this.orderService.handleFetchOrderByUser(user, pageable));
    }

    // fetch top selling product
    @GetMapping("/orders/top-selling")
    @ApiMessage("Fetch top selling products")
    public ResponseEntity<List<ResProductSalesDTO>> getTopSellingProducts(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate) {
        return ResponseEntity.ok(this.orderService.handleFetchTopSellingProducts(startDate, endDate));
    }

    // fetch slow selling product
    @GetMapping("/orders/slow-selling")
    @ApiMessage("Fetch slow selling products ")
    public ResponseEntity<List<ResProductSalesDTO>> getSlowSellingProducts(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        List<ResProductSalesDTO> result = orderService.handleFetchSlowSellingProducts(startDate, endDate);
        return ResponseEntity.ok(result);
    }

    // delete order by id
    @DeleteMapping("/orders/{id}")
    @ApiMessage("Delete order success")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) {
        this.orderService.handleDeleteOrderById(id);
        return ResponseEntity.ok(null);
    }

    // Fetch list order
    @GetMapping("/orders")
    @ApiMessage("Fetch all order")
    public ResponseEntity<ResultPaginationDTO> fetchOrders(
            @Filter Specification<Order> spec, Pageable pageable) {
        return ResponseEntity.ok(this.orderService.handleFetchOrders(spec, pageable));
    }

    // Fetch order by id
    @GetMapping("/orders/{id}")
    @ApiMessage("Fetch order by id")
    public ResponseEntity<List<OrderDetail>> fetchOrderDetailsById(@PathVariable("id") long id) {
        Optional<Order> order = this.orderRepository.findById(id);
        return ResponseEntity.ok(this.orderService.handleFetchOrderDetailById(order.get()));
    }

    // update order
    @PutMapping("/orders")
    @ApiMessage("Update order success")
    public ResponseEntity<Order> updateOrder(@RequestBody Order putOrder) throws IdInvalidException {
        Order order = this.orderService.updateOrder(putOrder);
        if (order == null) {
            throw new IdInvalidException("Đơn hàng với id = " + putOrder.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(order);
    }

    @GetMapping("/orders/by-id/{id}")
    @ApiMessage("Fetch order by id")
    public ResponseEntity<Order> fetchOrderById(@PathVariable("id") long id) {
        return ResponseEntity.ok(this.orderService.handleFetchOrderById(id));
    }
}
