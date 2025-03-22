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
import vn.congdubai.shopping.domain.Role;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.service.RoleService;
import vn.congdubai.shopping.util.annotation.ApiMessage;
import vn.congdubai.shopping.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getRoles(
            @Filter Specification<Role> spec, Pageable pageable) {

        return ResponseEntity.ok(this.roleService.handleFetchRoles(spec, pageable));
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException {

        Role role = this.roleService.handleFetchRoleById(id);
        if (role == null) {
            throw new IdInvalidException("Vai trò với id = " + id + " không tồn tại");
        }

        return ResponseEntity.ok().body(role);
    }

    @PostMapping("/roles")
    @ApiMessage("Create role success")
    public ResponseEntity<Role> createRole(@Valid @RequestBody Role posRole) throws IdInvalidException {
        // TODO: process POST request
        boolean isNameExist = this.roleService.existByName(posRole.getName());
        if (isNameExist) {
            throw new IdInvalidException(
                    "Tên Vai trò " + posRole.getName() + "đã tồn tại, vui lòng sử dụng tên khác.");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.handleCreateRole(posRole));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete role success")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") long id) throws IdInvalidException {
        Role pro = this.roleService.handleFetchRoleById(id);
        if (pro == null) {
            throw new IdInvalidException("Vai trò với Id " + id + " Không tồn tại.");
        }
        this.roleService.handleDeleteRole(id);
        return ResponseEntity.ok(null);
    }

    @PutMapping("/roles")
    @ApiMessage("Update role success")
    public ResponseEntity<Role> updateRole(@Valid @RequestBody Role putRole) throws IdInvalidException {
        // TODO: process PUT request
        Role role = this.roleService.handleUpdateRole(putRole);
        if (role == null) {
            throw new IdInvalidException("Vai trò với id = " + putRole.getId() + " không tồn tại");
        }
        return ResponseEntity.ok(role);
    }
}
