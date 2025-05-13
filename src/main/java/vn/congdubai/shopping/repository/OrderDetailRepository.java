package vn.congdubai.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.OrderDetail;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, JpaSpecificationExecutor<OrderDetail> {
    @Query(value = "SELECT COALESCE(SUM(quantity), 0) FROM shopping_data.tblorder " +
            "INNER JOIN shopping_data.tblorder_detail ON shopping_data.tblorder.id = shopping_data.tblorder_detail.order_id "
            + "WHERE order_date BETWEEN :startDate AND :endDate", nativeQuery = true)
    long countQuantityOrderByDay(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    List<OrderDetail> findByOrder(Order order);
}
