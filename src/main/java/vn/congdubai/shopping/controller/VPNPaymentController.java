// package vn.congdubai.shopping.controller;

// import com.google.gson.Gson;
// import com.google.gson.GsonBuilder;
// import com.google.gson.JsonObject;
// import jakarta.servlet.http.HttpServletRequest;
// import vn.congdubai.shopping.config.VNPayConfig;
// import vn.congdubai.shopping.domain.Order;
// import vn.congdubai.shopping.domain.response.ResResponse;
// import vn.congdubai.shopping.repository.OrderRepository;
// import vn.congdubai.shopping.service.EmailService;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.RequestMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.RestController;

// import java.io.IOException;
// import java.net.URLEncoder;
// import java.nio.charset.StandardCharsets;
// import java.text.SimpleDateFormat;
// import java.time.LocalDateTime;
// import java.util.*;

// @RestController
// @RequestMapping("/api/v1")
// public class VPNPaymentController {

// private final OrderRepository orderRepository;
// private final EmailService emailService;

// public VPNPaymentController(OrderRepository orderRepository, EmailService
// emailService) {
// this.orderRepository = orderRepository;
// this.emailService = emailService;
// }

// @GetMapping("/create-payment")
// public ResponseEntity<?> createPayment(HttpServletRequest req,
// @RequestParam("price") double totalPrice,
// @RequestParam("orderId") long orderId) throws IOException {

// String vnp_TxnRef = VNPayConfig.getRandomNumber(8);
// String vnp_IpAddr = VNPayConfig.getIpAddress(req);

// String vnp_TmnCode = VNPayConfig.vnp_TmnCode;

// Map<String, String> vnp_Params = new HashMap<>();
// vnp_Params.put("vnp_Version", VNPayConfig.vnp_Version);
// vnp_Params.put("vnp_Command", VNPayConfig.vnp_Command);
// vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
// vnp_Params.put("vnp_totalPrice", String.valueOf(totalPrice));
// vnp_Params.put("vnp_CurrCode", "VND");
// vnp_Params.put("vnp_BankCode", "NCB");
// vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
// vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang:" + vnp_TxnRef);
// vnp_Params.put("vnp_OrderType", VNPayConfig.orderType);
// vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
// vnp_Params.put("vnp_Locale", "vn");
// String locate = req.getParameter("language");
// vnp_Params.put("vnp_ReturnUrl", VNPayConfig.vnp_ReturnUrl + "?orderId=" +
// orderId);

// Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
// SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
// String vnp_CreateDate = formatter.format(cld.getTime());
// vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

// cld.add(Calendar.MINUTE, 15);
// String vnp_ExpireDate = formatter.format(cld.getTime());
// vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

// List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
// Collections.sort(fieldNames);
// StringBuilder hashData = new StringBuilder();
// StringBuilder query = new StringBuilder();
// Iterator<String> itr = fieldNames.iterator();
// while (itr.hasNext()) {
// String fieldName = itr.next();
// String fieldValue = vnp_Params.get(fieldName);
// if (fieldValue != null && fieldValue.length() > 0) {
// // Build hash data
// hashData.append(fieldName);
// hashData.append('=');
// hashData.append(URLEncoder.encode(fieldValue,
// StandardCharsets.US_ASCII.toString()));
// // Build query
// query.append(URLEncoder.encode(fieldName,
// StandardCharsets.US_ASCII.toString()));
// query.append('=');
// query.append(URLEncoder.encode(fieldValue,
// StandardCharsets.US_ASCII.toString()));
// if (itr.hasNext()) {
// query.append('&');
// hashData.append('&');
// }
// }
// }
// String queryUrl = query.toString();
// String vnp_SecureHash = VNPayConfig.hmacSHA512(VNPayConfig.secretKey,
// hashData.toString());
// queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
// String paymentUrl = VNPayConfig.vnp_PayUrl + "?" + queryUrl;

// Gson gson = new GsonBuilder().disableHtmlEscaping().create();
// JsonObject job = new JsonObject();
// job.addProperty("code", "00");
// job.addProperty("message", "success");
// job.addProperty("data", paymentUrl);
// return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(job));
// }

// @GetMapping("/payment-infor")
// public ResponseEntity<?> transaction(@RequestParam(value = "vnp_totalPrice")
// String totalPrice,
// @RequestParam(value = "vnp_PayDate") String payDate,
// @RequestParam(value = "vnp_TransactionStatus") String responseCode,
// @RequestParam(value = "orderId") String orderId) {
// if (responseCode.equals("00")) {
// Order order = orderRepository.findById(Long.parseLong(orderId)).orElse(null);
// if (order != null) {
// order.setStatus("Đã thanh toán");
// order.setOrderDate(LocalDateTime.now());
// order.setTotalPrice(Double.parseDouble(totalPrice));
// order.setPaymentMethod("vnpay");
// orderRepository.save(order);

// // Gửi email hóa đơn
// emailService.sendBookingInvoice(order);
// }

// // Trả về phản hồi
// ResResponse res = new ResResponse();
// res.setStatusCode(200);
// res.setMessage("Payment successful");
// res.setData(Map.of(
// "bookingId", orderId,
// "totalPrice", totalPrice,
// "paymentDate", payDate,
// "status", responseCode));
// return ResponseEntity.status(HttpStatus.OK).body(res);
// } else {
// ResResponse res = new ResResponse();
// res.setStatusCode(404);
// res.setError("Payment failed, please try again.");
// return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
// }
// }

// }