package vn.congdubai.shopping.controller.admin;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import vn.congdubai.shopping.domain.CartDetail;
import vn.congdubai.shopping.domain.Discount;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.DiscountRepository;
import vn.congdubai.shopping.service.DiscountService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class DiscountController {

    private final DiscountRepository discountRepository;
    private final DiscountService discountService;

    public DiscountController(DiscountService discountService, DiscountRepository discountRepository) {
        this.discountService = discountService;
        this.discountRepository = discountRepository;
    }

    @GetMapping("/discounts")
    @ApiMessage("Fetch discounts")
    public ResponseEntity<ResultPaginationDTO> getDiscounts(Pageable pageable) {
        return ResponseEntity.ok(this.discountService.handleFetchDiscounts(pageable));
    }

    @PostMapping("/discounts")
    @ApiMessage("Create discounts success")
    public ResponseEntity<Discount> createDiscount(@Valid @RequestBody Discount posDiscount) throws IdInvalidException {
        // TODO: process POST request
        boolean isNameExist = this.discountService.existByName(posDiscount.getCode());
        if (isNameExist) {
            throw new IdInvalidException(
                    "Tên mã giảm giá " + posDiscount.getCode() + "đã tồn tại, vui lòng sử dụng tên khác.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.discountService.handleCreateDiscount(posDiscount));
    }

    @DeleteMapping("/discounts/{id}")
    @ApiMessage("Delete discount success")
    public ResponseEntity<Void> deleteDiscount(@PathVariable("id") long id) throws IdInvalidException {
        Discount discount = this.discountService.handleFetchDiscountById(id);
        if (discount == null) {
            throw new IdInvalidException("Mã giảm giá với Id " + id + " Không tồn tại.");
        }
        this.discountService.handleDeleteDiscount(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/discounts")
    @ApiMessage("Update discounts success")
    public ResponseEntity<Discount> updateSize(@Valid @RequestBody Discount putDiscount) throws IdInvalidException {
        // TODO: process PUT request
        Discount discount = this.discountService.handleUpdateDiscount(putDiscount);
        if (discount == null) {
            throw new IdInvalidException("Mã giảm giá với id = " + putDiscount.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(discount);
    }

    @GetMapping("/discounts/discounts-top3")
    @ApiMessage("Fetch discounts top 3")
    public ResponseEntity<List<Discount>> getDiscountsTop3() {
        return ResponseEntity.ok(this.discountService.handleFetchTop3Discount());
    }

    @GetMapping("/apply-discount")
    @ApiMessage("apply discount")
    public double applyDiscount(@RequestParam("code") String code,
            @RequestParam("totalPrice") double totalPrice) throws IdInvalidException {

        Discount discount = discountService.handleFetchDiscountByCode(code);
        if (discount == null || discount.getEndDate().isBefore(LocalDateTime.now())) {
            throw new IdInvalidException("Mã giảm giá không hợp lệ hoặc đã hết hạn!");
        }

        if (discount.getQuantity() <= 0) {
            throw new IdInvalidException("Mã giảm giá đã hết!");
        }

        double discountAmount = Math.min(totalPrice * discount.getDiscountPercent() / 100, discount.getMaxDiscount());
        double finalPrice = totalPrice - discountAmount;

        discount.setQuantity(discount.getQuantity() - 1);
        this.discountRepository.save(discount);
        return finalPrice;
    }

}
