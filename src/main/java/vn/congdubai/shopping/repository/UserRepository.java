package vn.congdubai.shopping.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import vn.congdubai.shopping.domain.User;

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

}
