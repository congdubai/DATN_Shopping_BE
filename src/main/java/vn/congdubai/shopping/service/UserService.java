package vn.congdubai.shopping.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import vn.congdubai.shopping.domain.Role;
import vn.congdubai.shopping.domain.User;
import vn.congdubai.shopping.domain.response.PasswordDTO;
import vn.congdubai.shopping.domain.response.ResCreateUserDTO;
import vn.congdubai.shopping.domain.response.ResUpdateUserDTO;
import vn.congdubai.shopping.domain.response.ResUserDTO;
import vn.congdubai.shopping.domain.response.ResultPaginationDTO;
import vn.congdubai.shopping.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, RoleService roleService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    public Specification<User> notDeletedSpec() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isDeleted"), false);
    }

    // fetch all user
    public ResultPaginationDTO getAllUser(Specification<User> spec, Pageable pageable) {
        Specification<User> notDeletedSpec = notDeletedSpec().and(spec);
        Page<User> pUser = this.userRepository.findAll(notDeletedSpec, pageable);

        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta mt = new ResultPaginationDTO.Meta();
        mt.setPage(pageable.getPageNumber() + 1);
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pUser.getTotalPages());
        mt.setTotal(pUser.getTotalElements());
        rs.setMeta(mt);

        List<ResUserDTO> listUser = pUser.getContent()
                .stream().map(item -> this.convertToResUserDTO(item))
                .collect(Collectors.toList());
        rs.setResult(listUser);
        return rs;
    }

    // convert user to ResUserDTO
    public ResUserDTO convertToResUserDTO(User user) {
        ResUserDTO res = new ResUserDTO();
        ResUserDTO.RoleUser role = new ResUserDTO.RoleUser();

        res.setId(user.getId());
        res.setAvatar(user.getAvatar());
        res.setEmail(user.getEmail());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        if (user.getRole() != null) {
            role.setId(user.getRole().getId());
            role.setName(user.getRole().getName());
            res.setRole(role);
        }
        return res;
    }

    // create new user
    public User createUser(User user) {
        if (user.getRole() != null) {
            Role role = this.roleService.handleFetchRoleById(user.getRole().getId());
            user.setRole(role != null ? role : null);
        }
        Role role = this.roleService.handleFetchRoleByName("USER");
        user.setRole(role);
        return this.userRepository.save(user);
    }

    // Convert to ResCreateUserDTO
    public ResCreateUserDTO convertToResCreateUserDTO(User user) {
        ResCreateUserDTO res = new ResCreateUserDTO();

        res.setId(user.getId());
        res.setName(user.getName());
        res.setAvatar(user.getAvatar());
        res.setEmail(user.getEmail());
        res.setAge(user.getAge());
        res.setCreatedAt(user.getCreatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());

        return res;
    }

    // fetch user by id
    public User fetchUserById(long id) {
        Optional<User> userOptional = this.userRepository.findById(id);
        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        return null;
    }

    // check email Exist
    public boolean isEmailExist(String email) {
        return this.userRepository.existsByEmail(email);
    }

    // delete a user
    public void deleteUser(long id) {
        this.userRepository.softDeleteUser(id);
    }

    // update a user
    public User UpdateUser(User user) {
        User userCurrent = this.fetchUserById(user.getId());
        if (userCurrent != null) {
            userCurrent.setAddress(user.getAddress());
            userCurrent.setGender(user.getGender());
            userCurrent.setAvatar(user.getAvatar());
            userCurrent.setAge(user.getAge());
            userCurrent.setName(user.getName());
            if (user.getRole() != null) {
                Role r = this.roleService.handleFetchRoleById(user.getRole().getId());
                userCurrent.setRole(r != null ? r : null);
            }
            this.userRepository.save(userCurrent);
        }
        return userCurrent;
    }

    // convert to ResCreateUserDTO
    public ResUpdateUserDTO convertToResUpdateUserDTO(User user) {
        ResUpdateUserDTO res = new ResUpdateUserDTO();
        res.setId(user.getId());
        res.setName(user.getName());
        res.setAge(user.getAge());
        res.setAvatar(user.getAvatar());
        res.setUpdatedAt(user.getUpdatedAt());
        res.setGender(user.getGender());
        res.setAddress(user.getAddress());
        return res;
    }

    // Get user by username
    public User handleGetUserByUsername(String email) {
        return this.userRepository.findByEmail(email);
    }

    public void updateUserToken(String token, String email) {
        User currentUser = this.handleGetUserByUsername(email);
        if (currentUser != null) {
            currentUser.setRefreshToken(token);
            this.userRepository.save(currentUser);
        }
    }

    public User getUserByRefreshTokenAndEmail(String token, String email) {
        return this.userRepository.findByRefreshTokenAndEmail(token, email);
    }

    public void changePassword(PasswordDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new RuntimeException("Không tìm thấy người dùng!");
        }

        // So sánh mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu cũ không đúng");
        }

        // Mã hóa và cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
