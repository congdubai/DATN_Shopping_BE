package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.congdubai.shopping.controller.client.AuthController;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.CategoryRepository;
import vn.congdubai.shopping.service.ProductService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final AuthController authController;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductService productService, CategoryRepository categoryRepository,
            AuthController authController) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.authController = authController;
    }

    @GetMapping("/products")
    @ApiMessage("Fetch all products")
    public ResponseEntity<ResultPaginationDTO> fetchProducts(
            @Filter Specification<Product> spec, Pageable pageable) {
        return ResponseEntity.ok(this.productService.handleFetchProducts(spec, pageable));
    }

    @PostMapping("/products")
    @ApiMessage("Create product success")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product posProduct) throws IdInvalidException {
        // TODO: process POST request
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.handleCreateProduct(posProduct));
    }

}
