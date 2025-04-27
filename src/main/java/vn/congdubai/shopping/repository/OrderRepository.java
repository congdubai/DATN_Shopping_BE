package vn.congdubai.shopping.repository;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    @Query(value = "SELECT COALESCE(SUM(total_price), 0) FROM shopping_data.tblorder WHERE DATE(order_date) = CURRENT_DATE", nativeQuery = true)
    double getTotalPriceByDay();

    Page<Order> findByUser(User user, Pageable pageable);

    // lấy đơn hàng gần nhất theo ngày
    @Query(value = "SELECT * FROM shopping_data.tblorder WHERE DATE(order_date) = CURRENT_DATE ORDER BY order_date DESC", nativeQuery = true)
    List<Order> getCurrentOrderByDay();
}
