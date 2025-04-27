package vn.congdubai.shopping.service;

import org.springframework.stereotype.Service;
import vn.congdubai.shopping.domain.Review;
import vn.congdubai.shopping.repository.ReviewRepository;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
}
