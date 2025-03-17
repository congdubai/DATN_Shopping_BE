package vn.congdubai.shopping.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.CategoryRepository;
import vn.congdubai.shopping.repository.ProductRepository;
import vn.congdubai.shopping.util.error.IdInvalidException;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, FileService fileService,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    // Fetch all products
    public ResultPaginationDTO handleFetchProducts(Specification<Product> spec, Pageable pageable) {
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

    // Create new product
    public Product handleCreateProduct(Product product) throws IdInvalidException {
        Category category = categoryRepository.findById(product.getCategory().getId())
                .orElseThrow(() -> new IdInvalidException("Danh mục chưa tồn tại hoặc chưa nhập."));
        product.setCategory(category);

        return productRepository.save(product);
    }

}
