package vn.congdubai.shopping.controller.client;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.Review;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.ProductService;
import vn.congdubai.shopping.service.ReviewService;
import vn.congdubai.shopping.util.SecurityUtil;
import vn.congdubai.shopping.util.annotation.ApiMessage;

@RestController
@RequestMapping("/api/v1")
public class ReviewController {
    private final ProductService productService;
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService, ProductService productService) {
        this.reviewService = reviewService;
        this.productService = productService;
    }

    @PostMapping("/review")
    @ApiMessage("add Review success")
    public ResponseEntity<Review> addReview(@RequestBody Review review) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reviewService.saveReview(review));
    }

    @GetMapping("/review")
    @ApiMessage("Fetch all review by product")
    public ResponseEntity<ResultPaginationDTO> fetchReview(@RequestParam("id") long productId,
            Pageable pageable) {
        Product product = productService.handleFetchProductById(productId);
        return ResponseEntity.ok(this.reviewService.handleFetchReviewByProduct(product, pageable));
    }
}
