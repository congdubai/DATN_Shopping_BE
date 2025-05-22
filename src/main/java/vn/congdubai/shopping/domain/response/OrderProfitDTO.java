package vn.congdubai.shopping.domain.response;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class OrderProfitDTO {
    private String customerName;
    private String phone;
    private Long quantity;
    private Double totalPrice;
    private Double minPrice;
    private LocalDateTime orderDate;
    private Double profit;

    public OrderProfitDTO(String customerName, String phone, Long quantity, Double totalPrice,
            Double minPrice, LocalDateTime orderDate, Double profit) {
        this.customerName = customerName;
        this.phone = phone;
        this.quantity = quantity;
        this.totalPrice = totalPrice;
        this.minPrice = minPrice;
        this.orderDate = orderDate;
        this.profit = profit;
    }
}
