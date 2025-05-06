package vn.congdubai.shopping.domain.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResProductSalesDTO {
    private long productId;
    private String productName;
    private String productImage;
    private long totalQuantitySold;
    private double productPrice;
    private double averageRating;

    public ResProductSalesDTO(long productId, String productName, String productImage,
            long totalQuantitySold, double productPrice, double averageRating) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.totalQuantitySold = totalQuantitySold;
        this.productPrice = productPrice;
        this.averageRating = averageRating;
    }

}
