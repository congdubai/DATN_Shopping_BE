package vn.congdubai.shopping.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import vn.congdubai.shopping.domain.Cart;
import vn.congdubai.shopping.domain.CartDetail;
import vn.congdubai.shopping.domain.ProductDetail;

@Repository
public interface CartDetailRepository extends JpaRepository<CartDetail, Long>, JpaSpecificationExecutor<CartDetail> {
    CartDetail findByCartAndProductDetail(Cart cart, ProductDetail productDetail);

}
