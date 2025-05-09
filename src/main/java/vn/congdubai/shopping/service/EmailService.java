package vn.congdubai.shopping.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import vn.congdubai.shopping.domain.Order;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service
public class EmailService {

    private final JavaMailSender javaMailSender;

    public EmailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendVerificationEmail(String to, String subject, String body) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom("doanhvipnvn@gmail.com", "Hotel Luxeoasis");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (MessagingException e) {
            // Ghi log lỗi (nếu bạn có logger)
            System.err.println("Không thể gửi email đến " + to + ": " + e.getMessage());
            throw new RuntimeException("Không thể gửi email. Vui lòng thử lại sau.");
        } catch (UnsupportedEncodingException e) {
            System.err.println("Lỗi mã hóa tiêu đề người gửi: " + e.getMessage());
            throw new RuntimeException("Lỗi khi xử lý người gửi.");
        }
    }

    public void sendCouponExp(String to) {
        String subject = "Hotel Luxeoasis";
        Date today = new Date();
        // List<CouponDTO> activeCoupons = couponService.findCouponsByExpiryDate(today);

        // Tạo nội dung email với CSS
        StringBuilder body = new StringBuilder();
        body.append("<!DOCTYPE html>")
                .append("<html lang=\"en\">")
                .append("<head>")
                .append("<meta charset=\"UTF-8\">")
                .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
                .append("<title>Ưu đãi từ Hotel Luxeoasis</title>")
                .append("<style>")
                .append("body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 20px; }")
                .append(".container { background-color: #ffffff; border-radius: 8px; padding: 20px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }")
                .append("h1 { color: #333; }")
                .append("p { color: #555; }")
                .append("ul { list-style-type: none; padding: 0; }")
                .append("li { background-color: #f9f9f9; margin: 10px 0; padding: 10px; border-radius: 4px; border: 1px solid #e0e0e0; }")
                .append("strong { color: #007bff; }")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<div class=\"container\">");
        body.append("</ul>")
                .append("<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>");

        body.append("</div>")
                .append("</body>")
                .append("</html>");

        // Gửi email
        sendVerificationEmail(to, subject, body.toString());
    }

    @Async
    public void sendBookingInvoice(Order order) {
        String subject = "Hóa đơn đặt phòng #" + order.getId();
        String body = generateInvoiceHtml(order);

        sendVerificationEmail(order.getOrderEmail(), subject, body);
    }

    private String generateInvoiceHtml(Order order) {
        // Mẫu HTML hóa đơn
        String htmlTemplate = "<!DOCTYPE html>" +
                "<html lang=\"en\">" +
                "<head>" +
                "<meta charset=\"UTF-8\">" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                "<title>Hóa đơn đặt phòng</title>" +
                "<style>" +
                "body {" +
                "font-family: Arial, sans-serif;" +
                "margin: 0;" +
                "padding: 0;" +
                "background-color: #f4f4f4;" +
                "}" +
                ".invoice-container {" +
                "width: 80%;" +
                "margin: auto;" +
                "background-color: #ffffff;" +
                "padding: 20px;" +
                "border-radius: 10px;" +
                "box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);" +
                "}" +
                ".header {" +
                "text-align: center;" +
                "border-bottom: 1px solid #cccccc;" +
                "margin-bottom: 20px;" +
                "}" +
                ".header h1 {" +
                "margin: 0;" +
                "}" +
                ".header p {" +
                "margin: 5px 0;" +
                "}" +
                ".invoice-details {" +
                "margin-bottom: 20px;" +
                "}" +
                ".invoice-details h3 {" +
                "margin: 0 0 10px 0;" +
                "color: #333333;" +
                "}" +
                ".invoice-details p {" +
                "margin: 5px 0;" +
                "color: #555555;" +
                "}" +
                ".booking-details, .payment-details {" +
                "margin-bottom: 20px;" +
                "}" +
                ".booking-details h3, .payment-details h3 {" +
                "margin-bottom: 10px;" +
                "color: #333333;" +
                "}" +
                ".booking-details p, .payment-details p {" +
                "margin: 5px 0;" +
                "color: #555555;" +
                "}" +
                ".footer {" +
                "text-align: center;" +
                "margin-top: 20px;" +
                "border-top: 1px solid #cccccc;" +
                "padding-top: 10px;" +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class=\"invoice-container\">" +
                "<div class=\"header\">" +
                "<h1>HÓA ĐƠN THANH TOÁN</h1>" +
                "<p>Cảm ơn quý khách đã sử dụng dịch vụ của chúng tôi!</p>" +
                "</div>" +
                "<div class=\"order\">" +
                "<h3>Thông tin Đơn hàng</h3>" +
                "<p><strong>Mã đơn hàng:</strong> {{id}}</p>" +
                "<p><strong>Tên người dùng:</strong> {{receiverName}}</p>" +
                "<p><strong>Số điện thoại:</strong> {{receiverPhone}}</p>" +
                "<h3>Chi tiết thanh toán</h3>" +
                "<p><strong>Tổng số tiền:</strong> {{totalPrice}} VND</p>" +
                "<p><strong>Phương thức thanh toán:</strong> Thanh toán online</p>" +
                "<p><strong>Ngày thanh toán:</strong> {{orderDate}}</p>" +
                "<p><strong>Trạng thái:</strong> Thành công</p>" +
                "</div>" +
                "<div class=\"footer\">" +
                "<p>Chúng tôi rất mong được đón tiếp quý khách!</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

        // Thay thế các biến trong HTML bằng dữ liệu thực tế
        return htmlTemplate
                .replace("{{id}}", String.valueOf(order.getId()))
                .replace("{{receiverName}}", order.getReceiverName())
                .replace("{{receiverPhone}}", order.getReceiverPhone())
                .replace("{{totalPrice}}", String.format("%,.2f", order.getTotalPrice()))
                .replace("{{orderDate}}", new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
    }

}
