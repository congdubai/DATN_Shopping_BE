package vn.congdubai.shopping.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.response.OrderProfitDTO;
import vn.congdubai.shopping.domain.response.ResCategorySalesDTO;
import vn.congdubai.shopping.domain.response.ResSaleChannelSummaryDTO;
import vn.congdubai.shopping.domain.response.TopUserStatisticDTO;
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

    // lấy danh sách top nhân viên bán hàng
    public List<TopUserStatisticDTO> handleFetchTopSellerByDay(LocalDateTime startDate, LocalDateTime endDate) {
        return this.userRepository.getTopUsersByDateRange(startDate, endDate);
    }

    // lấy dữ liệu bán hàng
    public List<Map<String, Object>> getRevenueByChannel(String startDate, String endDate) {
        List<Map<String, Object>> monthlyRevenue = orderRepository.getMonthlyRevenueByChannel(startDate, endDate);
        List<String> months = Arrays.asList("T1", "T2", "T3", "T4", "T5", "T6", "T7", "T8", "T9", "T10", "T11", "T12");
        Map<String, Map<String, Object>> revenueMap = new HashMap<>();
        for (Map<String, Object> entry : monthlyRevenue) {
            String month = (String) entry.get("date");
            String country = (String) entry.get("country");
            String monthNumber = convertMonthToNumber(month);
            revenueMap.putIfAbsent(monthNumber, new HashMap<>());
            revenueMap.get(monthNumber).put(country, entry.get("value"));
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (String month : months) {
            Map<String, Object> dataOnline = new HashMap<>();
            dataOnline.put("date", month);
            dataOnline.put("country", "Online");
            dataOnline.put("value", revenueMap.getOrDefault(month, new HashMap<>()).getOrDefault("Online", 0));

            Map<String, Object> dataOffline = new HashMap<>();
            dataOffline.put("date", month);
            dataOffline.put("country", "Tại cửa hàng");
            dataOffline.put("value", revenueMap.getOrDefault(month, new HashMap<>()).getOrDefault("Tại cửa hàng", 0));

            // Thêm dữ liệu vào kết quả
            result.add(dataOnline);
            result.add(dataOffline);
        }

        // Trả về kết quả
        return result;
    }

    private String convertMonthToNumber(String month) {
        Map<String, String> monthMap = new HashMap<>();
        monthMap.put("Jan", "T1");
        monthMap.put("Feb", "T2");
        monthMap.put("Mar", "T3");
        monthMap.put("Apr", "T4");
        monthMap.put("May", "T5");
        monthMap.put("Jun", "T6");
        monthMap.put("Jul", "T7");
        monthMap.put("Aug", "T8");
        monthMap.put("Sep", "T9");
        monthMap.put("Oct", "T10");
        monthMap.put("Nov", "T11");
        monthMap.put("Dec", "T12");

        return monthMap.getOrDefault(month, month);
    }

    // lấy Tổng số tiền theo ngày
    public Double handleFetchTotalPriceByDay(LocalDateTime startDate, LocalDateTime endDate) {
        return this.orderRepository.getTotalRevenue(startDate, endDate);
    }

    public List<OrderProfitDTO> handleFetchOrderProfit(LocalDateTime startDate, LocalDateTime endDate) {
        return this.orderRepository.getOrderProfitsByDateAndStatus(startDate, endDate);

    }
}
