package vn.congdubai.shopping.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "product_details")
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @NotNull
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private long quantity;

    private String imageDetail;
}
