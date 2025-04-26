package vn.congdubai.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import vn.congdubai.shopping.domain.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long>, JpaSpecificationExecutor<OrderDetail> {
    @Query(value = "SELECT COALESCE(SUM(quantity), 0) FROM shopping_data.tblorder inner join  shopping_data.tblorder_detail ON shopping_data.tblorder.id = shopping_data.tblorder_detail.order_id WHERE DATE(order_date) = CURRENT_DATE", nativeQuery = true)
    long countQuantityOrderByDay();
}
