package vn.congdubai.shopping.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;

import vn.congdubai.shopping.domain.Category;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.CategoryService;
import vn.congdubai.shopping.util.annotation.ApiMessage;

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
}
