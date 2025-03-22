package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.congdubai.shopping.controller.client.AuthController;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.CategoryRepository;
import vn.congdubai.shopping.service.ProductService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService, CategoryRepository categoryRepository,
            AuthController authController) {
        this.productService = productService;
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
        boolean isNameExist = this.productService.existByName(posProduct.getName());
        if (isNameExist) {
            throw new IdInvalidException(
                    "Tên sản phẩm " + posProduct.getName() + "đã tồn tại, vui lòng sử dụng tên khác.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.handleCreateProduct(posProduct));
    }

    @DeleteMapping("/products/{id}")
    @ApiMessage("Delete product success")
    public ResponseEntity<Void> deleteProduct(@PathVariable("id") long id) throws IdInvalidException {
        Product pro = this.productService.handleFetchProductById(id);
        if (pro == null) {
            throw new IdInvalidException("sản phẩm với Id " + id + " Không tồn tại.");
        }
        this.productService.handleDeleteProduct(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/products")
    @ApiMessage("Update product success")
    public ResponseEntity<Product> updateProduct(@Valid @RequestBody Product putProduct) throws IdInvalidException {
        // TODO: process PUT request
        Product product = this.productService.handleUpdateProduct(putProduct);
        if (product == null) {
            throw new IdInvalidException("Sản phẩm với id = " + putProduct.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(product);
    }
}
