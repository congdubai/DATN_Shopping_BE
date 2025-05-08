package vn.congdubai.shopping.service;

import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.OrderDetail;
import vn.congdubai.shopping.domain.Review;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.ResOrderHistoryDTO;
import vn.congdubai.shopping.domain.response.ResProductSalesDTO;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.OrderDetailRepository;
import vn.congdubai.shopping.repository.OrderRepository;
import vn.congdubai.shopping.repository.ReviewRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ReviewRepository reviewRepository;

    public OrderService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository,
            ReviewRepository reviewRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.reviewRepository = reviewRepository;
    }

    // Fetch history order
    public ResOrderHistoryDTO mapToResOrderHistoryDTO(OrderDetail orderDetail) {
        ResOrderHistoryDTO dto = new ResOrderHistoryDTO();
        dto.setId(orderDetail.getId());
        dto.setProductId(orderDetail.getProductDetail().getProduct().getId());
        dto.setOrderId(orderDetail.getOrder().getId());
        dto.setName(orderDetail.getProductDetail().getProduct().getName());
        dto.setImage(orderDetail.getProductDetail().getImageDetail());
        dto.setSize(orderDetail.getSize());
        dto.setColor(orderDetail.getColor());
        dto.setQuantity(orderDetail.getQuantity());
        dto.setPrice(orderDetail.getPrice());
        dto.setStatus(orderDetail.getOrder().getStatus());
        dto.setRating(orderDetail.getOrder().isRating());
        dto.setUserId(orderDetail.getOrder().getUser().getId());
        return dto;
    }

    // Fetch list order by user
    public ResultPaginationDTO handleFetchOrderByUser(User user, Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findByUser(user, pageable);

        List<OrderDetail> allOrderDetails = ordersPage.getContent().stream()
                .flatMap(order -> order.getOrderDetails().stream())
                .collect(Collectors.toList());

        List<ResOrderHistoryDTO> listDTO = allOrderDetails.stream()
                .map(this::mapToResOrderHistoryDTO)
                .collect(Collectors.toList());

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(ordersPage.getTotalPages());
        mt.setTotal(ordersPage.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(listDTO);

        return rs;
    }

    // Fetch list order
    public ResultPaginationDTO handleFetchOrders(Pageable pageable) {
        Page<Order> ordersPage = orderRepository.findAll(pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(ordersPage.getTotalPages());
        mt.setTotal(ordersPage.getTotalElements());
        rs.setMeta(mt);
        rs.setResult(ordersPage.get());

        return rs;
    }

    // delete a order
    public void handleDeleteOrderById(long id) {
        Optional<Order> orderOptional = this.orderRepository.findById(id);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            List<OrderDetail> orderDetails = order.getOrderDetails();
            List<Review> review = order.getReviews();
            for (OrderDetail orderDetail : orderDetails) {
                this.orderDetailRepository.deleteById(orderDetail.getId());
            }
            for (Review item : review) {
                this.reviewRepository.deleteById(item.getId());
            }
        }

        this.orderRepository.deleteById(id);
    }

    public Order updateOrder(Order order) {
        Optional<Order> orderOptional = orderRepository.findById(order.getId());
        if (orderOptional.isEmpty()) {
            throw new EntityNotFoundException("Order with ID " + order.getId() + " not found.");
        }
        Order currentOrder = orderOptional.get();
        currentOrder.setStatus(order.getStatus());
        return orderRepository.save(currentOrder);
    }

    // Fetch orderDetail by order
    public List<OrderDetail> handleFetchOrderDetailById(Order order) {
        return this.orderDetailRepository.findByOrder(order);
    }

    // Fetch Top selling product
    public List<ResProductSalesDTO> handleFetchTopSellingProducts(LocalDateTime startDate, LocalDateTime endDate) {
        List<ResProductSalesDTO> topSellingProductsPage = orderRepository.findTopSellingProducts(startDate, endDate);
        return topSellingProductsPage;
    }

    // Fetch Slow selling product
    public List<ResProductSalesDTO> handleFetchSlowSellingProducts(LocalDateTime fromDate, LocalDateTime toDate) {
        LocalDateTime minCreatedDate = toDate.minusDays(10);

        return orderRepository.findLowSalesProducts(minCreatedDate, fromDate, toDate);
    }

}
