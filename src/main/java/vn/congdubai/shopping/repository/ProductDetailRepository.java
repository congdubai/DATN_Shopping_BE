package vn.congdubai.shopping.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.ProductDetail;
import java.util.List;

@Repository
public interface ProductDetailRepository
                extends JpaRepository<ProductDetail, Long>, JpaSpecificationExecutor<ProductDetail> {

        @Modifying
        @Transactional
        @Query("UPDATE ProductDetail p SET p.isDeleted = true WHERE p.id = :id")
        void softDeleteProductDetail(@Param("id") Long id);

        @Query("SELECT pd FROM ProductDetail pd WHERE pd.product.id = :productId AND pd.color.id = :colorId AND pd.size.id = :sizeId")
        Optional<ProductDetail> findByProductIdAndColorIdAndSizeId(@Param("productId") Long productId,
                        @Param("colorId") Long colorId,
                        @Param("sizeId") Long sizeId);

        List<ProductDetail> findByProduct(Product product);
}
