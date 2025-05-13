package vn.congdubai.shopping.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.response.ResCategorySalesDTO;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    boolean existsByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.isDeleted = true WHERE c.id = :id")
    void softDeleteCategory(@Param("id") Long id);

    @Query(value = """
            SELECT
                c.name AS type,
                COALESCE(SUM(od.quantity), 0) AS value
            FROM
                tblcategory c
            JOIN
                tblproduct p ON p.category_id = c.id
            JOIN
                tblproduct_detail pd ON pd.product_id = p.id
            JOIN
                tblorder_detail od ON od.product_detail_id = pd.id
            JOIN
                tblorder o ON o.id = od.order_id
            WHERE
                o.status != 'Đã hủy'
                AND o.order_date BETWEEN :startDate AND :endDate
            GROUP BY
                c.name
            """, nativeQuery = true)
    List<ResCategorySalesDTO> getSalesByCategoryBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
