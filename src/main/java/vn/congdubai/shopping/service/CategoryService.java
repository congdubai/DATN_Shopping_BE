package vn.congdubai.shopping.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.CategoryRepository;

@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    public Specification<Category> notDeletedSpec() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), false);
    }

    // Fetch all category
    public ResultPaginationDTO handleFetchCategories(Specification<Category> spec, Pageable pageable) {
        Specification<Category> notDeletedSpec = notDeletedSpec().and(spec);
        Page<Category> pCategory = this.categoryRepository.findAll(notDeletedSpec, pageable);
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

    // Fetch all category
    public List<Category> handleFetchAllCategories() {
        Specification<Category> notDeletedSpec = notDeletedSpec();
        List<Category> pCategory = this.categoryRepository.findAll(notDeletedSpec);
        return pCategory;
    }

    // Fetch category by id
    public Category handleFetchCategoryById(long id) {
        Optional<Category> categoryOptional = this.categoryRepository.findById(id);
        if (categoryOptional.isPresent())
            return categoryOptional.get();
        return null;
    }

    // Check exist By Name
    public boolean existByName(String name) {
        return this.categoryRepository.existsByName(name);
    }

    // Create new category
    public Category handleCreateCategory(Category category) {
        return this.categoryRepository.save(category);
    }

    // Update category
    public Category handleUpdateCategory(Category category) {
        Category currentCategory = this.handleFetchCategoryById(category.getId());
        if (currentCategory != null) {
            currentCategory.setName(category.getName());
            currentCategory.setGender(category.getGender());
            currentCategory.setDescription(category.getDescription());
            currentCategory.setImage(category.getImage());
            this.categoryRepository.save(currentCategory);
        }
        return currentCategory;
    }

    // Delete category
    public void handleDeleteCategory(long id) {
        this.categoryRepository.softDeleteCategory(id);
    }

}
