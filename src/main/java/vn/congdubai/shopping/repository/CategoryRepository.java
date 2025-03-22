package vn.congdubai.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.transaction.Transactional;
import vn.congdubai.shopping.domain.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>, JpaSpecificationExecutor<Category> {
    boolean existsByName(String name);

    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.isDeleted = true WHERE c.id = :id")
    void softDeleteCategory(@Param("id") Long id);
}
