package vn.congdubai.shopping.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long>, JpaSpecificationExecutor<Product> {
    Page<Review> findByProduct(Product product, Pageable pageable);
}
