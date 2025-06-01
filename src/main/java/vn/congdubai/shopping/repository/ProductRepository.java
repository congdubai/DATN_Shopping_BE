package vn.congdubai.shopping.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.response.ProductDTO;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.isDeleted = true WHERE p.id = :id")
    void softDeleteProduct(@Param("id") Long id);

    boolean existsByName(String name);

    @Query("SELECT p FROM Product p WHERE p.isDeleted = false AND p.category.gender = :gender")
    Page<Product> findByCategoryGender(@Param("gender") vn.congdubai.shopping.util.constant.GenderEnum gender,
            Pageable pageable);

    List<Product> findByNameContainingIgnoreCase(String name);

    @Query("""
                SELECT new vn.congdubai.shopping.domain.response.ProductDTO(
                    p.id,
                    p.name,
                    p.price,
                    MAX(c.name),
                    COALESCE(AVG(r.rating), 0.0),
                    p.image
                )
                FROM Product p
                LEFT JOIN p.productDetails pd
                LEFT JOIN pd.color c
                LEFT JOIN p.reviews r
                WHERE (:categoryId IS NULL OR p.category.id = :categoryId)
                  AND (p.price BETWEEN :minPrice AND :maxPrice)
                  AND (:colorId IS NULL OR c.id = :colorId)
                  AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))
                GROUP BY p.id, p.name, p.price, p.image
                HAVING (:rating IS NULL OR COALESCE(AVG(r.rating), 0.0) >= :rating)
            """)
    List<ProductDTO> searchProducts(
            @Param("categoryId") Long categoryId,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("colorId") Long colorId,
            @Param("rating") Long rating,
            @Param("name") String name);

}
