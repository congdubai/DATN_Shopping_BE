package vn.congdubai.shopping.controller.admin;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.turkraft.springfilter.boot.Filter;

import jakarta.validation.Valid;
import vn.congdubai.shopping.domain.Color;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.ColorService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class ColorController {

    private final ColorService colorService;

    public ColorController(ColorService colorService) {
        this.colorService = colorService;
    }

    @GetMapping("/colors")
    @ApiMessage("Fetch colors")
    public ResponseEntity<ResultPaginationDTO> getColors(
            @Filter Specification<Color> spec, Pageable pageable) {

        return ResponseEntity.ok(this.colorService.handleFetchColors(spec, pageable));
    }

    @GetMapping("/colors/{id}")
    @ApiMessage("Fetch color by id")
    public ResponseEntity<Color> getById(@PathVariable("id") long id) throws IdInvalidException {

        Color color = this.colorService.handleFetchColorById(id);
        if (color == null) {
            throw new IdInvalidException("Màu sắc với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(color);
    }

    @PostMapping("/colors")
    @ApiMessage("Create color success")
    public ResponseEntity<Color> createColor(@Valid @RequestBody Color posColor) throws IdInvalidException {
        // TODO: process POST request
        boolean isNameExist = this.colorService.existByName(posColor.getName());
        if (isNameExist) {
            throw new IdInvalidException(
                    "Tên màu sắc " + posColor.getName() + "đã tồn tại, vui lòng sử dụng tên khác.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.colorService.handleCreateColor(posColor));
    }

    @DeleteMapping("/colors/{id}")
    @ApiMessage("Delete color success")
    public ResponseEntity<Void> deleteColor(@PathVariable("id") long id) throws IdInvalidException {
        Color pro = this.colorService.handleFetchColorById(id);
        if (pro == null) {
            throw new IdInvalidException("Màu sắc với Id " + id + " Không tồn tại.");
        }
        this.colorService.handleDeleteColor(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/colors")
    @ApiMessage("Update color success")
    public ResponseEntity<Color> updateColor(@Valid @RequestBody Color putColor) throws IdInvalidException {
        // TODO: process PUT request
        Color color = this.colorService.handleUpdateColor(putColor);
        if (color == null) {
            throw new IdInvalidException("Màu sắc với id = " + putColor.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(color);
    }
}
