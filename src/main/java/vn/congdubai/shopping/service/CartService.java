package vn.congdubai.shopping.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpSession;
import vn.congdubai.shopping.domain.Cart;
import vn.congdubai.shopping.domain.CartDetail;
import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.OrderDetail;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.ProductDetail;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.repository.CartDetailRepository;
import vn.congdubai.shopping.repository.CartRepository;
import vn.congdubai.shopping.repository.OrderDetailRepository;
import vn.congdubai.shopping.repository.OrderRepository;
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
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    public CartService(UserService userService, CartRepository cartRepository, ProductRepository productRepository,
            ProductDetailRepository productDetailRepository, CartDetailRepository cartDetailRepository,
            OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
        this.userService = userService;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.productDetailRepository = productDetailRepository;
        this.cartDetailRepository = cartDetailRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
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

    public Order handlePlaceOrder(
            User user, String receiverName, String receiverAddress, String receiverPhone, String paymentMethod,
            String uuid, Double finalPrice) {
        Order currentOrder = new Order();
        // Step 1: Get cart by user
        Cart cart = this.cartRepository.findByUserId(user.getId());
        if (cart != null) {
            List<CartDetail> cartDetails = cart.getCartDetails();

            if (cartDetails != null) {

                // Create order
                Order order = new Order();
                order.setUser(user);
                order.setReceiverName(receiverName);
                order.setReceiverAddress(receiverAddress);
                order.setReceiverPhone(receiverPhone);
                order.setOrderDate(LocalDateTime.now());
                order.setStatus("Đang xử lý");
                order.setPaymentMethod(paymentMethod);
                order.setPaymentStatus("PAYMENT_UNPAID");

                order.setPaymentRef(paymentMethod.equals("COD") ? "UNKNOWN" : uuid);

                if (finalPrice != null) {
                    order.setTotalPrice(finalPrice); // Set final price after discount
                } else {
                    double sum = 0;
                    for (CartDetail cd : cartDetails) {
                        sum += cd.getPrice() * cd.getQuantity();
                    }
                    order.setTotalPrice(sum);
                }

                currentOrder = this.orderRepository.save(order);

                // Create orderDetail for each product in the cart
                for (CartDetail cd : cartDetails) {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setProductDetail(cd.getProductDetail());
                    orderDetail.setPrice(cd.getPrice());
                    orderDetail.setQuantity(cd.getQuantity());
                    orderDetail.setColor(cd.getColor());
                    orderDetail.setSize(cd.getSize());
                    this.orderDetailRepository.save(orderDetail);

                    ProductDetail productDetail = cd.getProductDetail();
                    if (productDetail.getQuantity() >= cd.getQuantity()) {
                        productDetail.setQuantity(productDetail.getQuantity() - cd.getQuantity());
                        this.productDetailRepository.save(productDetail);
                    } else {
                        throw new IllegalStateException("Số lượng sản phẩm trong kho không đủ.");
                    }
                }

                // Step 2: Delete cart_detail and cart
                for (CartDetail cd : cartDetails) {
                    this.cartDetailRepository.deleteById(cd.getId());
                }

                this.cartRepository.deleteById(cart.getId());
            }
        }
        return currentOrder;
    }

    public void handleUpdateQuantity(long cartDetailId, long quantity) {
        Optional<CartDetail> cartDetailOptional = this.cartDetailRepository.findById(cartDetailId);
        if (cartDetailOptional.isPresent()) {
            CartDetail cartDetail = cartDetailOptional.get();
            cartDetail.setQuantity(quantity);
            this.cartDetailRepository.save(cartDetail);
        }
    }
}
