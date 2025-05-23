package vn.congdubai.shopping.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.OrderProfitDTO;
import vn.congdubai.shopping.domain.response.ResProductSalesDTO;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
        @Query(value = "SELECT COALESCE(SUM(total_price), 0) FROM shopping_data.tblorder WHERE order_date BETWEEN :startDate AND :endDate", nativeQuery = true)
        double getTotalPriceByDay(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

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

        @Query("""
                        SELECT new vn.congdubai.shopping.domain.response.ResProductSalesDTO(
                            pd.id,
                            p.name,
                            pd.imageDetail,
                            COALESCE(SUM(od.quantity), 0),
                            MIN(p.price),
                            COALESCE((
                                SELECT AVG(r.rating)
                                FROM Review r
                                WHERE r.product.id = p.id
                            ), 0)
                        )
                        FROM ProductDetail pd
                        JOIN pd.product p
                        LEFT JOIN OrderDetail od ON od.productDetail.id = pd.id
                        LEFT JOIN Order o ON od.order.id = o.id
                        WHERE
                            p.isDeleted = false
                            AND pd.isDeleted = false
                            AND pd.quantity >= 5
                            AND pd.createdAt <= :minCreatedDate
                            AND (o.orderDate BETWEEN :fromDate AND :toDate OR o.orderDate IS NULL)
                        GROUP BY
                            pd.id, p.name, pd.imageDetail, pd.createdAt
                        HAVING
                            COALESCE(SUM(od.quantity), 0) <= 5
                        """)
        List<ResProductSalesDTO> findLowSalesProducts(
                        @Param("minCreatedDate") LocalDateTime minCreatedDate,
                        @Param("fromDate") LocalDateTime fromDate,
                        @Param("toDate") LocalDateTime toDate);

        Optional<Order> findByPaymentRef(String paymentRef);

        @Query(value = "SELECT COALESCE(SUM(quantity), 0) FROM shopping_data.tblorder " +
                        "INNER JOIN shopping_data.tblorder_detail ON shopping_data.tblorder.id = shopping_data.tblorder_detail.order_id "
                        +
                        "WHERE order_date BETWEEN :startDate AND :endDate AND status = 'Đã hủy'", nativeQuery = true)
        long countQuantityCancelOrderByDay(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query(value = """
                        SELECT
                            DATE_FORMAT(o.order_date, '%b') AS date,
                            CASE
                                WHEN o.payment_method = 'COD_OFFLINE' THEN 'Tại cửa hàng'
                                ELSE 'Online'
                            END AS country,
                            SUM(o.total_price) AS value
                        FROM tblorder o
                        WHERE o.order_date BETWEEN :startDate AND :endDate
                        GROUP BY DATE_FORMAT(o.order_date, '%b'),
                                 CASE WHEN o.payment_method = 'COD_OFFLINE' THEN 'Tại cửa hàng' ELSE 'Online' END,
                                 MONTH(o.order_date)
                        ORDER BY MONTH(o.order_date)
                        """, nativeQuery = true)
        List<Map<String, Object>> getMonthlyRevenueByChannel(
                        @Param("startDate") String startDate,
                        @Param("endDate") String endDate);

        @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderDate BETWEEN :startDate AND :endDate")
        Double getTotalRevenue(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

        @Query("""
                            SELECT new vn.congdubai.shopping.domain.response.OrderProfitDTO(
                                o.receiverName,
                                o.receiverPhone,
                                od.quantity,
                                o.totalPrice,
                                p.minPrice,
                                o.orderDate,
                                (o.totalPrice - p.minPrice * od.quantity)
                            )
                            FROM Order o
                            JOIN o.orderDetails od
                            JOIN od.productDetail pd
                            JOIN pd.product p
                            WHERE o.orderDate BETWEEN :startDate AND :endDate
                              AND o.status = 'Đã hoàn thành'
                        """)
        List<OrderProfitDTO> getOrderProfitsByDateAndStatus(
                        @Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

}
