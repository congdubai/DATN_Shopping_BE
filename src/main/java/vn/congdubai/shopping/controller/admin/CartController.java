package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.congdubai.shopping.domain.Cart;
import vn.congdubai.shopping.domain.CartDetail;
import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.ResCartDetailDTO;
import vn.congdubai.shopping.domain.response.RestResponse;
import vn.congdubai.shopping.service.CartService;
import vn.congdubai.shopping.service.UserService;
import vn.congdubai.shopping.util.SecurityUtil;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

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

        RestResponse<Void> response = new RestResponse<>();
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

        return ResponseEntity.ok(dtoList); // Trả về danh sách chi tiết giỏ hàng

    }

    @DeleteMapping("/cart/{id}")
    @ApiMessage("Delete category success")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") long id) throws IdInvalidException {
        long cartDetailId = id;
        this.cartService.handleRemoveCartDetail(cartDetailId);
        return ResponseEntity.ok(null);
    }

}
