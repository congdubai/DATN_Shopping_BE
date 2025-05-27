package vn.congdubai.shopping.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Product> {
    Page<Review> findByProduct(Product product, Pageable pageable);

    @Query("SELECT r.product.id AS productId, AVG(r.rating) AS avgRating " +
            "FROM Review r WHERE r.product.id IN :productIds GROUP BY r.product.id")
    List<Object[]> findAverageRatingsForProducts(@Param("productIds") List<Long> productIds);

}
