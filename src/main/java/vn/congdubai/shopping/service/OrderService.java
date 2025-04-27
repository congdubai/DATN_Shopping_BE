package vn.congdubai.shopping.service;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.OrderDetail;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.ResOrderHistoryDTO;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.OrderRepository;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

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
        return dto;
    }

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

}
