package vn.congdubai.shopping.controller.admin;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import vn.congdubai.shopping.controller.client.AuthController;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.response.ProductDTO;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.CategoryRepository;
import vn.congdubai.shopping.repository.ProductRepository;
import vn.congdubai.shopping.service.ProductService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.constant.GenderEnum;
import vn.congdubai.shopping.util.error.IdInvalidException;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

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

    private final ProductRepository productRepository;
    private final ProductService productService;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductService productService, CategoryRepository categoryRepository,
            AuthController authController, ProductRepository productRepository) {
        this.productService = productService;
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
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

    @GetMapping("/products/by-category/{categoryId}")
    @ApiMessage("Fetch products by category")
    public ResponseEntity<ResultPaginationDTO> fetchProductsByCategory(
            @PathVariable Long categoryId,
            @Filter Specification<Product> spec,
            Pageable pageable) {
        Category category = this.categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Category không tồn tại"));

        return ResponseEntity.ok(productService.handleFetchProductsByCategory(spec, pageable, category));
    }

    @GetMapping("/products/{gender}")
    @ApiMessage("Fetch products by gender")
    public ResponseEntity<ResultPaginationDTO> fetchProductsByGender(
            @PathVariable String gender,
            Pageable pageable) {

        GenderEnum genderEnum;
        try {
            genderEnum = GenderEnum.valueOf(gender);
        } catch (IllegalArgumentException e) {
            ResultPaginationDTO errorResponse = new ResultPaginationDTO();
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Trả về kết quả phân trang
        return ResponseEntity.ok(productService.handleFetchProductsByGender(genderEnum, pageable));
    }

    @GetMapping("/products/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String name) {
        List<Product> products = productService.searchProductsByName(name);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/products/search-query")
    public ResponseEntity<List<ProductDTO>> searchProductByQuery(
            @RequestParam(name = "query", required = false) String query) {
        // Giá mặc định nếu không truyền range
        String name = "";
        Long category = null;
        Double minPrice = 0.0;
        Double maxPrice = Double.MAX_VALUE;
        Long rating = null;
        Long colors = null;

        if (query != null) {
            try {
                // Giải mã chuỗi query
                String decodedQuery = URLDecoder.decode(query, StandardCharsets.UTF_8.name());

                // Tách chuỗi query thành các tham số
                String[] params = decodedQuery.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    if (keyValue.length == 2) {
                        String key = keyValue[0];
                        String value = keyValue[1];

                        // Gán các tham số vào các biến tương ứng
                        if ("category".equals(key)) {
                            category = Long.valueOf(value);
                        } else if ("priceRange".equals(key)) {
                            String[] priceArray = value.split("-");
                            if (priceArray.length == 2) {
                                try {
                                    minPrice = Double.valueOf(priceArray[0].trim());
                                    maxPrice = Double.valueOf(priceArray[1].trim());
                                } catch (NumberFormatException e) {
                                    System.out.println("Lỗi chuyển đổi khoảng giá: " + e.getMessage());
                                }
                            }
                        } else if ("colors".equals(key)) {
                            colors = Long.valueOf(value);
                        } else if ("rating".equals(key)) {
                            rating = Long.valueOf(value);
                        } else if ("name".equals(key)) {
                            name = value; // Lưu giá trị name từ query
                        }
                    }
                }
            } catch (Exception e) {
                // Xử lý lỗi nếu có
                System.out.println("Lỗi khi phân tích query string: " + e.getMessage());
            }
        }
        // Tìm kiếm sản phẩm với các điều kiện đã được lọc, bao gồm tên sản phẩm
        List<ProductDTO> result = productRepository.searchProducts(category, minPrice, maxPrice, colors, rating, name);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/products/by-id/{id}")
    @ApiMessage("Fetch products by id")
    public ResponseEntity<Product> fetchProductsByGender(
            @PathVariable long id) {
        return ResponseEntity.ok(productService.handleFetchProductById(id));
    }

}
