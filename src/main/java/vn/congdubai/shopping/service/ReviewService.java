package vn.congdubai.shopping.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.Review;
import vn.congdubai.shopping.domain.response.ResReviewDTO;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.OrderRepository;
import vn.congdubai.shopping.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;

    public ReviewService(ReviewRepository reviewRepository, OrderRepository orderRepository) {
        this.reviewRepository = reviewRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public Review saveReview(Review review) {
        Long orderId = review.getOrder().getId();
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        order.setRating(true);
        review.setOrder(order);
        orderRepository.save(order);

        return reviewRepository.save(review);
    }

    public ResultPaginationDTO handleFetchReviewByProduct(Product product, Pageable pageable) {
        // Lấy danh sách các Review từ repository
        Page<Review> reviewsPage = reviewRepository.findByProduct(product, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(reviewsPage.getTotalPages());
        mt.setTotal(reviewsPage.getTotalElements());
        rs.setMeta(mt);

        // Chuyển đổi danh sách Review thành danh sách ResReviewDTO
        List<ResReviewDTO> reviewDTOList = reviewsPage.getContent().stream()
                .map(review -> {
                    ResReviewDTO resReviewDTO = new ResReviewDTO();
                    resReviewDTO.setId(review.getId());
                    resReviewDTO.setRating(review.getRating());
                    resReviewDTO.setComment(review.getComment());
                    resReviewDTO.setUserName(review.getUser().getName());
                    resReviewDTO.setAvatar(review.getUser().getAvatar());
                    resReviewDTO.setCreatedAt(review.getCreatedAt());
                    return resReviewDTO;
                })
                .collect(Collectors.toList());

        rs.setResult(reviewDTOList);
        return rs;
    }
}
