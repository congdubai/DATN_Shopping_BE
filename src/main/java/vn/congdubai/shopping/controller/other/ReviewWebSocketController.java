package vn.congdubai.shopping.controller.other;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import vn.congdubai.shopping.domain.Review;
import vn.congdubai.shopping.service.ReviewService;

@Controller
public class ReviewWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ReviewService reviewService;

    public ReviewWebSocketController(SimpMessagingTemplate messagingTemplate, ReviewService reviewService) {
        this.messagingTemplate = messagingTemplate;
        this.reviewService = reviewService;
    }

    @MessageMapping("/review")
    public void handleReview(Review review) {
        Review savedReview = reviewService.saveReview(review);
        messagingTemplate.convertAndSend("/topic/reviews", savedReview);
    }
}
