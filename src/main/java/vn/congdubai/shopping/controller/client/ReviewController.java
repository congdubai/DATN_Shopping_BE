package vn.congdubai.shopping.controller.client;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.congdubai.shopping.domain.Review;
import vn.congdubai.shopping.service.ReviewService;
import vn.congdubai.shopping.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/review")
    @ApiMessage("add Review success")
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.saveReview(review));
    }
}
