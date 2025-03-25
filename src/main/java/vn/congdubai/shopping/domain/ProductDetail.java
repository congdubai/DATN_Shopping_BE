package vn.congdubai.shopping.domain;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import vn.congdubai.shopping.util.SecurityUtil;

@Entity
@Table(name = "tblproduct_detail")
@Getter
@Setter
public class ProductDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @NotNull(message = "Sản phẩm không được để trống")
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @NotNull(message = "Size không được để trống")
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @ManyToOne
    @NotNull(message = "color không được để trống")
    @JoinColumn(name = "color_id", nullable = false)
    private Color color;

    @NotNull
    @Min(value = 0, message = "Số lượng phải lớn hơn hoặc bằng 0")
    private long quantity;

    private String imageDetail;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    private Instant createdAt;
    private Instant updatedAt;
    private String createdBy;
    private String updatedBy;

    @PrePersist
    public void handleBeforeCreate() {
        this.createdBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        setCreatedAt(Instant.now());
    }

    @PreUpdate
    public void handleBeforeUpdate() {
        this.updatedBy = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";
        setUpdatedAt(Instant.now());
    }
}
