package vn.congdubai.shopping.domain.response;

public interface TopUserStatisticDTO {
    String getName();

    String getEmail();

    String getAddress();

    String getPhone();

    Long getTotalQuantity();

    Double getTotalSpent();
}
