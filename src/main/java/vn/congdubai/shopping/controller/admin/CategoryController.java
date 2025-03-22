package vn.congdubai.shopping.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.CategoryService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class CategoryController {
    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping("/categories")
    @ApiMessage("Fetch all products")
    public ResponseEntity<ResultPaginationDTO> fetchCategories(
            @Filter Specification<Category> spec, Pageable pageable) {
        return ResponseEntity.ok(this.categoryService.handleFetchCategories(spec, pageable));
    }

    @PostMapping("/categories")
    @ApiMessage("Create category success")
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category posCategory) throws IdInvalidException {
        // TODO: process POST request
        boolean isNameExist = this.categoryService.existByName(posCategory.getName());
        if (isNameExist) {
            throw new IdInvalidException(
                    "Tên Danh mục " + posCategory.getName() + "đã tồn tại, vui lòng sử dụng tên khác.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.categoryService.handleCreateCategory(posCategory));
    }

    @DeleteMapping("/categories/{id}")
    @ApiMessage("Delete category success")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") long id) throws IdInvalidException {
        Category pro = this.categoryService.handleFetchCategoryById(id);
        if (pro == null) {
            throw new IdInvalidException("Danh mục với Id " + id + " Không tồn tại.");
        }
        this.categoryService.handleDeleteCategory(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/categories")
    @ApiMessage("Update category success")
    public ResponseEntity<Category> updateCategory(@Valid @RequestBody Category putCategory) throws IdInvalidException {
        Category category = this.categoryService.handleUpdateCategory(putCategory);
        if (category == null) {
            throw new IdInvalidException("Danh mục với id = " + putCategory.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(category);
    }
}