package vn.congdubai.shopping.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResOrderHistoryDTO {
    private long id;
    private long orderId;
    private long productId;
    private String name;
    private String image;
    private String size;
    private String color;
    private long quantity;
    private double price;
    private String status;
}
