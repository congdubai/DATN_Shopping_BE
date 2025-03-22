package vn.congdubai.shopping.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.Product;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // Fetch all category
    public ResultPaginationDTO handleFetchCategories(Specification<Category> spec, Pageable pageable) {
        Page<Category> pCategory = this.categoryRepository.findAll(spec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pCategory.getTotalPages());
        mt.setTotal(pCategory.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pCategory.getContent());
        return rs;
    }

    // Fetch category by id
    public Category handleFetchCategoryById(long id) {
        return this.categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy danh mục với ID: " + id));
    }

}
