package vn.congdubai.shopping.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.response.ResCategorySalesDTO;
import vn.congdubai.shopping.repository.CategoryRepository;
import vn.congdubai.shopping.repository.OrderDetailRepository;
import vn.congdubai.shopping.repository.OrderRepository;
import vn.congdubai.shopping.repository.UserRepository;

@Service
public class DashBoardService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final CategoryRepository categoryRepository;

    public DashBoardService(UserRepository userRepository, OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository, CategoryRepository categoryRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.categoryRepository = categoryRepository;
    }

    // Lấy người dùng theo ngày
    public long handleCountUserByDay(LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.countUsersByDay(startDate, endDate);
    }

    // Lấy số lượng bán hàng theo ngày
    public long handleCountOrderByDay(LocalDateTime startDate, LocalDateTime endDate) {
        return orderDetailRepository.countQuantityOrderByDay(startDate, endDate);
    }

    // Lấy tổng tiền hàng theo ngày
    public double handleGetTotalPriceByDay(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.getTotalPriceByDay(startDate, endDate);
    }

    // Lấy số lượng hủy theo ngày
    public long handleCountCancelOrderByDay(LocalDateTime startDate, LocalDateTime endDate) {
        return orderRepository.countQuantityCancelOrderByDay(startDate, endDate);
    }

    // lấy đơn hàng gần nhất theo ngày
    public List<Order> handleFetchCurrrentOrderByDay() {
        return this.orderRepository.getCurrentOrderByDay();
    }

    // lấy số lượng bán theo ngày
    public List<ResCategorySalesDTO> handleFetchCategorySales(LocalDateTime startDate, LocalDateTime endDate) {
        return this.categoryRepository.getSalesByCategoryBetween(startDate, endDate);
    }

}
