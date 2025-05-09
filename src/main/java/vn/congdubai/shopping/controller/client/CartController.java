package vn.congdubai.shopping.controller.client;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.congdubai.shopping.config.VNPayConfig;
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
import vn.congdubai.shopping.util.SecurityUtil;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
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

    public CartController(CartService cartService, UserService userService) {
        this.cartService = cartService;
        this.userService = userService;
    }

    @PostMapping("/add-to-cart")
    @ApiMessage("Add to cart success")
    public ResponseEntity<?> addProductToCart(@RequestParam long productId,
            @RequestParam long quantity,
            @RequestParam long color,
            @RequestParam long size,
            HttpSession session) {
        cartService.addProductToCart(productId, session, quantity, color, size);

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

    @RestController
    @RequestMapping("/api/v1")
    public class OrderController {

        private final CartService cartService;
        private final UserService userService;
        private final OrderRepository orderRepository;
        private final EmailService emailService;

        public OrderController(CartService cartService, UserService userService,
                OrderRepository orderRepository, EmailService emailService) {
            this.cartService = cartService;
            this.userService = userService;
            this.orderRepository = orderRepository;
            this.emailService = emailService;
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
                Order order = cartService.handlePlaceOrder(user, receiverName, receiverAddress, receiverPhone,
                        paymentMethod, uuid, totalPrice);

                // Tạo VNPay URL
                String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
                String vnp_IpAddr = VNPayConfig.getIpAddress(req);

                Map<String, String> vnp_Params = new HashMap<>();
                vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
                vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
                vnp_Params.put("vnp_TmnCode", VNPayConfig.vnp_TmnCode);
                vnp_Params.put("vnp_Amount", String.valueOf((long) (totalPrice * 100))); // nhân 100 theo VNPay yêu cầu
                vnp_Params.put("vnp_CurrCode", "VND");
                vnp_Params.put("vnp_BankCode", "NCB");
                vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
                vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang: " + vnp_TxnRef);
                vnp_Params.put("vnp_OrderType", VNPayConfig.orderType);
                vnp_Params.put("vnp_Locale", "vn");
                vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl + "?orderId=" + order.getId());
                vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

                Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
                SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
                vnp_Params.put("vnp_CreateDate", formatter.format(cld.getTime()));

                cld.add(Calendar.MINUTE, 15);
                vnp_Params.put("vnp_ExpireDate", formatter.format(cld.getTime()));

                List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
                Collections.sort(fieldNames);
                StringBuilder hashData = new StringBuilder();
                StringBuilder query = new StringBuilder();
                for (String fieldName : fieldNames) {
                    String fieldValue = vnp_Params.get(fieldName);
                    if (fieldValue != null && !fieldValue.isEmpty()) {
                        hashData.append(fieldName).append('=')
                                .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                        query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII))
                                .append('=')
                                .append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII))
                                .append('&');
                    }
                }

                query.setLength(query.length() - 1); // remove trailing '&'
                hashData.setLength(hashData.length() - 1);

                String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey, hashData.toString());
                String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + query + "&vnp_SecureHash=" + vnp_SecureHash;

                JsonObject job = new JsonObject();
                job.addProperty("statusCode", "00");
                job.addProperty("message", "success");
                job.addProperty("data", paymentUrl);

                Gson gson = new GsonBuilder().disableHtmlEscaping().create();
                return ResponseEntity.ok(gson.toJson(job));
            } else {
                // Thanh toán thường → xử lý luôn
                cartService.handlePlaceOrder(user, receiverName, receiverAddress, receiverPhone,
                        paymentMethod, uuid, totalPrice);
                return ResponseEntity.ok("Đặt hàng thành công");
            }
        }
    }

    @PutMapping("/cart")
    public ResponseEntity<Void> updateQuantity(@RequestParam("id") long id,
            @RequestParam("quantity") long quantity) {
        this.cartService.handleUpdateQuantity(id, quantity);
        return ResponseEntity.ok(null);
    }

}
