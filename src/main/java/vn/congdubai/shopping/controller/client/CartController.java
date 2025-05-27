package vn.congdubai.shopping.controller.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.congdubai.shopping.domain.Cart;
import vn.congdubai.shopping.domain.CartDetail;
import vn.congdubai.shopping.domain.Order;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.ResCartDetailDTO;
import vn.congdubai.shopping.domain.response.ResResponse;
import vn.congdubai.shopping.repository.OrderRepository;
import vn.congdubai.shopping.service.CartService;
import vn.congdubai.shopping.service.EmailService;
import vn.congdubai.shopping.service.UserService;
import vn.congdubai.shopping.service.VNPayService;
import vn.congdubai.shopping.util.SecurityUtil;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class CartController {
    private final CartService cartService;
    private final UserService userService;
    private final EmailService emailService;
    private final VNPayService vnPayService;

    public CartController(CartService cartService, UserService userService, EmailService emailService,
            VNPayService vnPayService) {
        this.cartService = cartService;
        this.userService = userService;
        this.emailService = emailService;
        this.vnPayService = vnPayService;
    }

    @PostMapping("/add-to-cart")
    @ApiMessage("Add to cart success")
    public ResponseEntity<?> addProductToCart(@RequestParam long productId,
            @RequestParam long quantity,
            @RequestParam long color,
            @RequestParam long size,
            HttpSession session) {
        cartService.addProductToCart(productId, quantity, color, size);

        ResResponse<Void> response = new ResResponse<>();
        response.setStatusCode(200);
        response.setMessage("Đã thêm sản phẩm vào giỏ hàng");
        response.setData(null);
        response.setError(null);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/cart")
    @ApiMessage("Fetch cart detail success")
    public ResponseEntity<List<ResCartDetailDTO>> getCartData() {
        Optional<String> optionalUsername = SecurityUtil.getCurrentUserLogin();
        User user = userService.handleGetUserByUsername(optionalUsername.get());
        Cart cart = cartService.fetchByUser(user);

        List<ResCartDetailDTO> dtoList = new ArrayList<>();
        if (cart != null) {
            for (CartDetail detail : cart.getCartDetails()) {
                ResCartDetailDTO dto = new ResCartDetailDTO();
                dto.setId(detail.getId());
                dto.setProductId(detail.getProductDetail().getProduct().getId());
                dto.setProductName(detail.getProductDetail().getProduct().getName());
                dto.setProductImage(detail.getProductDetail().getImageDetail());
                dto.setColorName(detail.getColor());
                dto.setSizeName(detail.getSize());
                dto.setPrice(detail.getProductDetail().getProduct().getPrice());
                dto.setQuantity(detail.getQuantity());
                dtoList.add(dto);
            }
        }

        return ResponseEntity.ok(dtoList);

    }

    @DeleteMapping("/cart/{id}")
    @ApiMessage("Delete category success")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") long id) throws IdInvalidException {
        long cartDetailId = id;
        this.cartService.handleRemoveCartDetail(cartDetailId);
        return ResponseEntity.ok(null);
    }

    @PostMapping("/place-order")
    public ResponseEntity<?> handlePlaceOrder(
            HttpServletRequest req,
            @RequestParam("receiverName") String receiverName,
            @RequestParam("receiverAddress") String receiverAddress,
            @RequestParam("receiverPhone") String receiverPhone,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("totalPrice") Double totalPrice) throws IOException {

        Optional<String> optionalUsername = SecurityUtil.getCurrentUserLogin();
        if (optionalUsername.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
        }

        User user = userService.handleGetUserByUsername(optionalUsername.get());
        final String uuid = UUID.randomUUID().toString().replace("-", "");

        if (paymentMethod.equalsIgnoreCase("vnpay")) {
            // Tạo Order tạm thời với trạng thái chờ thanh toán
            Order order = this.cartService.handlePlaceOrder(user, receiverName, receiverAddress, receiverPhone,
                    paymentMethod, uuid, totalPrice);
            String ip = this.vnPayService.getIpAddress(req);
            String vnpUrl = this.vnPayService.generateVNPayURL(totalPrice, uuid, ip);

            JsonObject job = new JsonObject();
            job.addProperty("statusCode", "00");
            job.addProperty("message", "success");
            job.addProperty("data", vnpUrl);
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            return ResponseEntity.ok(gson.toJson(job));
        } else {
            // Thanh toán thường → xử lý luôn
            this.cartService.handlePlaceOrder(user, receiverName, receiverAddress, receiverPhone,
                    paymentMethod, uuid, totalPrice);
            JsonObject job = new JsonObject();
            job.addProperty("statusCode", "200");
            job.addProperty("message", "success");
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            return ResponseEntity.ok(gson.toJson(job));
        }
    }

    @GetMapping("/thank")
    public ResponseEntity<?> handleVNPayReturn(
            @RequestParam("vnp_ResponseCode") Optional<String> vnpayResponseCode,
            @RequestParam("vnp_TxnRef") Optional<String> paymentRef) {

        if (vnpayResponseCode.isPresent() && paymentRef.isPresent()) {
            String status = vnpayResponseCode.get().equals("00") ? "PAYMENT_SUCCEED" : "PAYMENT_FAILED";
            Order order = cartService.updatePaymentStatus(paymentRef.get(), status);

            if (order != null && "PAYMENT_SUCCEED".equals(status)) {
                this.emailService.sendBookingInvoice(order);
            }

            return ResponseEntity.ok(Map.of(
                    "status", status,
                    "message", "Đã cập nhật trạng thái đơn hàng"));
        }

        return ResponseEntity.badRequest().body(Map.of(
                "status", "INVALID",
                "message", "Thiếu tham số hoặc không hợp lệ"));
    }

    @PutMapping("/cart")
    public ResponseEntity<Void> updateQuantity(@RequestParam("id") long id,
            @RequestParam("quantity") long quantity) {
        this.cartService.handleUpdateQuantity(id, quantity);
        return ResponseEntity.ok(null);
    }

}
