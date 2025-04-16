package vn.congdubai.shopping.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResCartDetailDTO {
    private Long id;
    private long productId;
    private String productName;
    private String productImage;
    private String colorName;
    private String sizeName;
    private double price;
    private long quantity;
}
