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
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.TopUserStatisticDTO;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(String email);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isDeleted = true WHERE u.id = :id")
    void softDeleteUser(@Param("id") Long id);

    User findByEmail(String email);

    User findByRefreshTokenAndEmail(String token, String email);

    @Query(value = "SELECT COUNT(*) FROM tbluser WHERE created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    long countUsersByDay(@Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query(value = """
                SELECT
                    u.name AS name,
                    u.email AS email,
                    u.address AS address,
                    u.phone AS phone,
                    SUM(od.quantity) AS totalQuantity,
                    SUM(od.quantity * od.price) AS totalSpent
                FROM
                    tbluser u
                JOIN
                    tblorder o ON u.id = o.user_id
                JOIN
                    tblorder_detail od ON o.id = od.order_id
                WHERE
                    o.order_date BETWEEN :startDate AND :endDate
                    AND o.payment_method = 'COD_OFFLINE'
                GROUP BY
                    u.id, u.name, u.email, u.address, u.phone
                ORDER BY
                    totalSpent DESC
            """, nativeQuery = true)
    List<TopUserStatisticDTO> getTopUsersByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

}
