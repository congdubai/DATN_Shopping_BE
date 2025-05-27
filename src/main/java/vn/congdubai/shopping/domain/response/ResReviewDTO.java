package vn.congdubai.shopping.domain.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResReviewDTO {
    private long id;
    private long rating;
    private String comment;
    private String userName;
    private String avatar;
    private LocalDateTime createdAt;
}
