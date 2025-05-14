package vn.congdubai.shopping.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDTO {
    private Long id;
    private String name;
    private Double price;
    private String colorName;
    private Double avgRating;
    private String image;

    public ProductDTO(Long id, String name, Double price, String colorName, Double avgRating, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.colorName = colorName;
        this.avgRating = avgRating;
        this.image = imageUrl;
    }

    // getters/setters
}
