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
import vn.congdubai.shopping.domain.Size;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.SizeService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SizeController {

    private final SizeService sizeService;

    public SizeController(SizeService sizeService) {
        this.sizeService = sizeService;
    }

    @GetMapping("/sizes")
    @ApiMessage("Fetch sizes")
    public ResponseEntity<ResultPaginationDTO> getSizes(
            @Filter Specification<Size> spec, Pageable pageable) {

        return ResponseEntity.ok(this.sizeService.handleFetchSizes(spec, pageable));
    }

    @GetMapping("/sizes/{id}")
    @ApiMessage("Fetch size by id")
    public ResponseEntity<Size> getById(@PathVariable("id") long id) throws IdInvalidException {

        Size size = this.sizeService.handleFetchSizeById(id);
        if (size == null) {
            throw new IdInvalidException("Size với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(size);
    }

    @PostMapping("/sizes")
    @ApiMessage("Create size success")
    public ResponseEntity<Size> createSize(@Valid @RequestBody Size posSize) throws IdInvalidException {
        // TODO: process POST request
        boolean isNameExist = this.sizeService.existByName(posSize.getName());
        if (isNameExist) {
            throw new IdInvalidException(
                    "Tên Size " + posSize.getName() + "đã tồn tại, vui lòng sử dụng tên khác.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.sizeService.handleCreateSize(posSize));
    }

    @DeleteMapping("/sizes/{id}")
    @ApiMessage("Delete size success")
    public ResponseEntity<Void> deleteSize(@PathVariable("id") long id) throws IdInvalidException {
        Size pro = this.sizeService.handleFetchSizeById(id);
        if (pro == null) {
            throw new IdInvalidException("Size với Id " + id + " Không tồn tại.");
        }
        this.sizeService.handleDeleteSize(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/sizes")
    @ApiMessage("Update size success")
    public ResponseEntity<Size> updateSize(@Valid @RequestBody Size putSize) throws IdInvalidException {
        // TODO: process PUT request
        Size size = this.sizeService.handleUpdateSize(putSize);
        if (size == null) {
            throw new IdInvalidException("Size với id = " + putSize.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(size);
    }
}
