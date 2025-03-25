package vn.congdubai.shopping.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.congdubai.shopping.domain.ProductDetail;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.ProductDetailService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ProductDetailDetailController {
    private final ProductDetailService productDetailService;

    public ProductDetailDetailController(ProductDetailService productDetailService) {
        this.productDetailService = productDetailService;
    }

    @GetMapping("/productDetails")
    @ApiMessage("Fetch all productDetails")
    public ResponseEntity<ResultPaginationDTO> fetchProductDetails(
            @Filter Specification<ProductDetail> spec, Pageable pageable) {
        return ResponseEntity.ok(this.productDetailService.handleFetchProductDetails(spec, pageable));
    }

    @PostMapping("/productDetails")
    @ApiMessage("Create productDetail success")
    public ResponseEntity<ProductDetail> createProductDetail(@Valid @RequestBody ProductDetail posProductDetail)
            throws IdInvalidException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(this.productDetailService.handleCreateProductDetail(posProductDetail));
    }

    @DeleteMapping("/productDetails/{id}")
    @ApiMessage("Delete productDetail success")
    public ResponseEntity<Void> deleteProductDetail(@PathVariable("id") long id) throws IdInvalidException {
        ProductDetail pro = this.productDetailService.handleFetchProductDetailById(id);
        if (pro == null) {
            throw new IdInvalidException("sản phẩm với Id " + id + " Không tồn tại.");
        }
        this.productDetailService.handleDeleteProductDetail(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/productDetails")
    @ApiMessage("Update productDetail success")
    public ResponseEntity<ProductDetail> updateProductDetail(@Valid @RequestBody ProductDetail putProductDetail)
            throws IdInvalidException {
        // TODO: process PUT request
        ProductDetail productDetail = this.productDetailService.handleUpdateProductDetail(putProductDetail);
        if (productDetail == null) {
            throw new IdInvalidException("Sản phẩm với id = " + putProductDetail.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(productDetail);
    }
}
