package vn.congdubai.shopping.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final FileService fileService;

    public ProductService(ProductRepository productRepository, FileService fileService) {
        this.productRepository = productRepository;
        this.fileService = fileService;
    }

    // fetch all user
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
}
