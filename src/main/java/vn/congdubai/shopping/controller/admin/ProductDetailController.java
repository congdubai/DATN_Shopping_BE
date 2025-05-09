package vn.congdubai.shopping.controller.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.congdubai.shopping.domain.Color;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.ProductDetail;
import vn.congdubai.shopping.domain.response.ResResponse;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.ColorService;
import vn.congdubai.shopping.service.ProductDetailService;
import vn.congdubai.shopping.service.ProductService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ProductDetailController {
    private final ProductDetailService productDetailService;
    private final ProductService productService;
    private final ColorService colorService;

    public ProductDetailController(ProductDetailService productDetailService, ProductService productService,
            ColorService colorService) {
        this.productDetailService = productDetailService;
        this.productService = productService;
        this.colorService = colorService;
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

    @GetMapping("/productDetailByColor/{productId}/{colorId}")
    @ApiMessage("Fetch productDetail by color success")
    public ResponseEntity<String> getProductDetailByColor(
            @PathVariable long productId,
            @PathVariable long colorId) {

        Product product = this.productService.handleFetchProductById(productId);
        Color color = this.colorService.handleFetchColorById(colorId);
        String imageName = productDetailService.handleGetProductDetailByProductAndColor(product, color);

        return ResponseEntity.ok(imageName != null ? imageName : "No image");
    }

    @GetMapping("/productDetails/by-product/{productId}")
    @ApiMessage("Fetch productDetail success")
    public ResponseEntity<List<ProductDetail>> getProductDetailsByProduct(@PathVariable("productId") long id) {
        Product product = productService.handleFetchProductById(id);
        return ResponseEntity.ok(productDetailService.handleGetProductDetailsByProduct(product));
    }

    @GetMapping("/productDetails/{id}")
    @ApiMessage("Fetch productDetail success")
    public ResponseEntity<ProductDetail> getProductDetailsById(@PathVariable("id") long id) {
        return ResponseEntity.ok(productDetailService.handleFetchProductDetailById(id));
    }
}
