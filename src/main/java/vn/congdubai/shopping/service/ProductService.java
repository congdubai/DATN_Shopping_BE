package vn.congdubai.shopping.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.ProductRepository;
import vn.congdubai.shopping.util.constant.GenderEnum;
import vn.congdubai.shopping.util.error.IdInvalidException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    public ProductService(ProductRepository productRepository, FileService fileService,
            CategoryService categoryService) {
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    public Specification<Product> notDeletedSpec() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), false);
    }

    // Fetch all products
    public ResultPaginationDTO handleFetchProducts(Specification<Product> spec, Pageable pageable) {
        Specification<Product> notDeletedSpec = notDeletedSpec().and(spec); // Kết hợp với spec của người dùng
        Page<Product> pProducts = this.productRepository.findAll(notDeletedSpec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pProducts.getTotalPages());
        mt.setTotal(pProducts.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pProducts.getContent());
        return rs;
    }

    public ResultPaginationDTO handleFetchProductsByCategory(Specification<Product> spec, Pageable pageable,
            Category category) {
        Specification<Product> categorySpec = (root, query, cb) -> cb.equal(root.get("category"), category);

        Specification<Product> finalSpec = notDeletedSpec().and(spec).and(categorySpec);

        Page<Product> pProducts = this.productRepository.findAll(finalSpec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pProducts.getTotalPages());
        mt.setTotal(pProducts.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pProducts.getContent());
        return rs;
    }

    // Fetch Product by id
    public Product handleFetchProductById(long id) {
        Optional<Product> productOptional = this.productRepository.findById(id);
        if (productOptional.isPresent()) {
            return productOptional.get();
        }
        return null;
    }

    // Check exist by name
    public boolean existByName(String name) {
        return this.productRepository.existsByName(name);
    }

    // Create new product
    public Product handleCreateProduct(Product product) {
        Category category = categoryService.handleFetchCategoryById(product.getCategory().getId());
        product.setCategory(category);
        return productRepository.save(product);
    }

    // Update product
    public Product handleUpdateProduct(Product product) throws IdInvalidException {
        Product currentProduct = this.handleFetchProductById(product.getId());
        if (currentProduct != null) {
            currentProduct.setName(product.getName());
            currentProduct.setPrice(product.getPrice());
            currentProduct.setImage(product.getImage());
            currentProduct.setShortDesc(product.getShortDesc());
            currentProduct.setDetailDesc(product.getDetailDesc());
            if (product.getCategory() != null) {
                Category category = this.categoryService.handleFetchCategoryById(product.getCategory().getId());
                currentProduct.setCategory(category != null ? category : null);
            }
            this.productRepository.save(currentProduct);
        }
        return currentProduct;
    }

    // delete a product
    public void handleDeleteProduct(long id) {
        this.productRepository.softDeleteProduct(id);
    }

    // fetch product by gender
    public ResultPaginationDTO handleFetchProductsByGender(GenderEnum gender, Pageable pageable) {
        // Tạo Specification lọc theo gender và không bị xóa
        Specification<Product> spec = notDeletedSpec()
                .and((root, query, cb) -> cb.equal(root.get("category").get("gender"), gender));

        Page<Product> pProducts = this.productRepository.findAll(spec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pProducts.getTotalPages());
        mt.setTotal(pProducts.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pProducts.getContent());
        return rs;
    }

}
