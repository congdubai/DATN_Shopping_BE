package vn.congdubai.shopping.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.congdubai.shopping.domain.Cart;
import vn.congdubai.shopping.domain.CartDetail;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.ProductDetail;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.repository.CartDetailRepository;
import vn.congdubai.shopping.repository.CartRepository;
import vn.congdubai.shopping.repository.ProductDetailRepository;
import vn.congdubai.shopping.repository.ProductRepository;
import vn.congdubai.shopping.util.SecurityUtil;

@Service
public class CartService {
    private final UserService userService;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductDetailRepository productDetailRepository;
    private final CartDetailRepository cartDetailRepository;
    // private final OrderRepository orderRepository;
    // private final OrderDetailRepository orderDetailRepository;

    public CartService(UserService userService, CartRepository cartRepository, ProductRepository productRepository,
            ProductDetailRepository productDetailRepository, CartDetailRepository cartDetailRepository) {
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.productDetailRepository = productDetailRepository;
        this.cartDetailRepository = cartDetailRepository;
        // this.orderRepository = orderRepository;
        // this.orderDetailRepository = orderDetailRepository;
    }

    public void addProductToCart(long productId, HttpSession session, long quantity, long colorId, long sizeId) {
        Optional<String> optionalUsername = SecurityUtil.getCurrentUserLogin();

        if (!optionalUsername.isPresent()) {
            // Nếu chưa đăng nhập, không xử lý server-side (xử lý ở frontend)
            return;
        }

        User user = userService.handleGetUserByUsername(optionalUsername.get());

        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUser(user);
            cart.setQuantity(0);
            cart = cartRepository.save(cart);
        }

        Optional<ProductDetail> productDetailOpt = productDetailRepository
                .findByProductIdAndColorIdAndSizeId(productId, colorId, sizeId);

        if (!productDetailOpt.isPresent())
            return;

        ProductDetail productDetail = productDetailOpt.get();

        Optional<Product> productOpt = productRepository.findById(productId);
        if (!productOpt.isPresent())
            return;

        Product product = productOpt.get();

        CartDetail existingDetail = cartDetailRepository.findByCartAndProductDetail(cart, productDetail);

        if (existingDetail == null) {
            CartDetail newDetail = new CartDetail();
            newDetail.setCart(cart);
            newDetail.setProductDetail(productDetail);
            newDetail.setPrice(product.getPrice());
            newDetail.setQuantity(quantity);
            newDetail.setColor(productDetail.getColor().getName());
            newDetail.setSize(productDetail.getSize().getName());
            cartDetailRepository.save(newDetail);

            cart.setQuantity(cart.getQuantity() + 1);
            cartRepository.save(cart);
            session.setAttribute("sum", cart.getQuantity());
        } else {
            existingDetail.setQuantity(existingDetail.getQuantity() + quantity);
            cartDetailRepository.save(existingDetail);
        }
    }

    public Cart fetchByUser(User user) {
        return this.cartRepository.findByUserId(user.getId());
    }

    public void handleRemoveCartDetail(long cartDetailId) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            Cart currentCart = cartDetail.getCart();
            // delete cart-detail
            this.cartDetailRepository.deleteById(cartDetailId);

            // update cart
            if (currentCart.getQuantity() > 1) {
                // update current cart
                long s = currentCart.getQuantity() - 1;
                currentCart.setQuantity(s);
                this.cartRepository.save(currentCart);
            } else {
                // delete cart (sum = 1)
                this.cartRepository.deleteById(currentCart.getId());
            }
        }
    }
}
