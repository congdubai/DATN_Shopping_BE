package vn.congdubai.shopping.domain;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tbldiscount")
@Getter
@Setter
public class Discount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull
    @NotEmpty(message = "Mã giảm giá không được để trống")
    @Column(unique = true)
    private String code;

    @NotNull
    @DecimalMin(value = "0", inclusive = true, message = "Phần trăm giảm giá phải từ 0% trở lên")
    @DecimalMax(value = "100", inclusive = true, message = "Phần trăm giảm giá phải từ 0% đến 100%")
    private double discountPercent;

    @NotNull
    @DecimalMin(value = "0", message = "Giảm giá tối đa phải lớn hơn hoặc bằng 0")
    private double maxDiscount;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime startDate;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime endDate;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Min(value = 0, message = "Số lượng mã giảm giá phải từ 0 trở lên")
    private long quantity;
}
