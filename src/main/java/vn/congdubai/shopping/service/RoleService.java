package vn.congdubai.shopping.service;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.congdubai.shopping.domain.Role;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepository;

    public RoleService(
            RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Specification<Role> notDeletedSpec() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), false);
    }

    // Fetch role by id
    public Role handleFetchRoleById(long id) {
        Optional<Role> roleOptional = this.roleRepository.findById(id);
        if (roleOptional.isPresent())
            return roleOptional.get();
        return null;
    }

    // Fetch all role
    public ResultPaginationDTO handleFetchRoles(Specification<Role> spec, Pageable pageable) {

        Specification<Role> notDeletedSpec = notDeletedSpec().and(spec);
        Page<Role> pRole = this.roleRepository.findAll(notDeletedSpec, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();

        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());

        mt.setPages(pRole.getTotalPages());
        mt.setTotal(pRole.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pRole.getContent());
        return rs;
    }

    // Check exist By Name
    public boolean existByName(String name) {
        return this.roleRepository.existsByName(name);
    }

    // Create new role
    public Role handleCreateRole(Role role) {
        return this.roleRepository.save(role);
    }

    public Role handleFetchRoleByName(String name) {
        return this.roleRepository.findByName(name);
    }

    // Update role
    public Role handleUpdateRole(Role role) {
        Role currentRole = this.handleFetchRoleById(role.getId());
        if (currentRole != null) {
            currentRole.setName(role.getName());
            currentRole.setDescription(role.getDescription());
            this.roleRepository.save(currentRole);
        }
        return currentRole;
    }

    // Delete role
    // delete a product
    public void handleDeleteRole(long id) {
        this.roleRepository.softDeleteRole(id);
    }
}
