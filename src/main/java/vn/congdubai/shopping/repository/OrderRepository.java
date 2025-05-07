package vn.congdubai.shopping.repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.ResProductSalesDTO;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
        @Query(value = "SELECT COALESCE(SUM(total_price), 0) FROM shopping_data.tblorder WHERE DATE(order_date) = CURRENT_DATE", nativeQuery = true)
        double getTotalPriceByDay();

        Page<Order> findByUser(User user, Pageable pageable);

        // lấy đơn hàng gần nhất theo ngày
        @Query(value = "SELECT * FROM shopping_data.tblorder WHERE DATE(order_date) = CURRENT_DATE ORDER BY order_date DESC", nativeQuery = true)
        List<Order> getCurrentOrderByDay();

        @Query("SELECT new vn.congdubai.shopping.domain.response.ResProductSalesDTO(" +
                        "pd.id, p.name, pd.imageDetail, " +
                        "SUM(od.quantity), MIN(p.price), COALESCE(AVG(r.rating), 0)) " +
                        "FROM OrderDetail od " +
                        "JOIN od.productDetail pd " +
                        "JOIN pd.product p " +
                        "JOIN od.order o " +
                        "LEFT JOIN Review r ON r.product.id = p.id " +
                        "WHERE p.isDeleted = false " +
                        "AND pd.isDeleted = false " +
                        "AND o.orderDate BETWEEN :startDate AND :endDate " +
                        "GROUP BY pd.id, p.name, pd.imageDetail " +
                        "ORDER BY SUM(od.quantity) DESC")
        List<ResProductSalesDTO> findTopSellingProducts(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

}
