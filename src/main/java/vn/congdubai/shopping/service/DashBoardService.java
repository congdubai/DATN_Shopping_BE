package vn.congdubai.shopping.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.OrderDetailRepository;
import vn.congdubai.shopping.repository.OrderRepository;
import vn.congdubai.shopping.repository.UserRepository;

@Service
public class DashBoardService {
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public DashBoardService(UserRepository userRepository, OrderRepository orderRepository,
            OrderDetailRepository orderDetailRepository) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
    }

    // Lấy người dùng theo ngày
    public long handleCountUserByDay() {
        return userRepository.countUsersByDay();
    }

    // Lấy số lượng bán hàng theo ngày
    public long handleCountOrderByDay() {
        return orderDetailRepository.countQuantityOrderByDay();
    }

    // Lấy tổng tiền hàng theo ngày
    public double handleGetTotalPriceByDay() {
        return orderRepository.getTotalPriceByDay();
    }

    // lấy đơn hàng gần nhất theo ngày
    public List<Order> handleFetchCurrrentOrderByDay() {
        return this.orderRepository.getCurrentOrderByDay();
    }

}
