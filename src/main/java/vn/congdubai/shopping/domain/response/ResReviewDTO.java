package vn.congdubai.shopping.domain.response;

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
}
