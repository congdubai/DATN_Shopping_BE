package vn.congdubai.shopping.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.Color;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.ProductDetail;
import vn.congdubai.shopping.domain.Size;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.ProductDetailRepository;
import vn.congdubai.shopping.util.error.IdInvalidException;

@Service
public class ProductDetailService {
    private final ProductDetailRepository productDetailRepository;
    private final ColorService colorService;
    private final ProductService productService;
    private final SizeService sizeService;

    public ProductDetailService(ProductDetailRepository productDetailRepository, ColorService colorService,
            ProductService productService, SizeService sizeService) {
        this.productDetailRepository = productDetailRepository;
        this.colorService = colorService;
        this.productService = productService;
        this.sizeService = sizeService;
    }

    public Specification<ProductDetail> notDeletedSpec() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), false);
    }

    // Fetch all productDetails
    public ResultPaginationDTO handleFetchProductDetails(Specification<ProductDetail> spec, Pageable pageable) {
        Specification<ProductDetail> notDeletedSpec = notDeletedSpec().and(spec);
        Page<ProductDetail> pProductDetails = this.productDetailRepository.findAll(notDeletedSpec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pProductDetails.getTotalPages());
        mt.setTotal(pProductDetails.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pProductDetails.getContent());
        return rs;
    }

    // Fetch productDetail by id
    public ProductDetail handleFetchProductDetailById(long id) {
        Optional<ProductDetail> pDetailOptional = this.productDetailRepository.findById(id);
        if (pDetailOptional.isPresent()) {
            return pDetailOptional.get();
        }
        return null;
    }

    // Create new productDetail
    public ProductDetail handleCreateProductDetail(ProductDetail productDetail) {
        Product product = this.productService.handleFetchProductById(productDetail.getProduct().getId());
        Color color = this.colorService.handleFetchColorById(productDetail.getColor().getId());
        Size size = this.sizeService.handleFetchSizeById(productDetail.getSize().getId());

        Optional<ProductDetail> existingDetail = productDetailRepository.findByProductIdAndColorIdAndSizeId(
                product.getId(), color.getId(), size.getId());

        if (existingDetail.isPresent()) {
            ProductDetail currentDetail = existingDetail.get();
            currentDetail.setQuantity(currentDetail.getQuantity() + productDetail.getQuantity());
            if (productDetail.getImageDetail() != null) {
                currentDetail.setImageDetail(productDetail.getImageDetail());
            }
            return productDetailRepository.save(currentDetail);
        }
        productDetail.setColor(color);
        productDetail.setSize(size);
        productDetail.setProduct(product);
        return this.productDetailRepository.save(productDetail);

    }

    // Update productDetail
    public ProductDetail handleUpdateProductDetail(ProductDetail pDetail) throws IdInvalidException {
        ProductDetail currentPDetail = this.handleFetchProductDetailById(pDetail.getId());
        if (currentPDetail != null) {
            currentPDetail.setImageDetail(pDetail.getImageDetail());
            currentPDetail.setQuantity(pDetail.getQuantity());
            if (pDetail.getColor() != null && pDetail.getProduct() != null && pDetail.getSize() != null) {
                Product product = this.productService.handleFetchProductById(pDetail.getProduct().getId());
                Color color = this.colorService.handleFetchColorById(pDetail.getColor().getId());
                Size size = this.sizeService.handleFetchSizeById(pDetail.getSize().getId());

                currentPDetail.setColor(color != null ? color : null);
                currentPDetail.setProduct(product != null ? product : null);
                currentPDetail.setSize(size != null ? size : null);
            }
        }
        return this.productDetailRepository.save(currentPDetail);
    }

    // delete a productDetail
    public void handleDeleteProductDetail(long id) {
        this.productDetailRepository.softDeleteProductDetail(id);
    }

    // fetch product detail by product
    public List<ProductDetail> handleGetProductDetailsByProduct(Product product) {
        return productDetailRepository.findByProduct(product);
    }
}
