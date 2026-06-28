package ra.quan_ly_khoa_hoc.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ra.quan_ly_khoa_hoc.exception.BadRequestException;
import ra.quan_ly_khoa_hoc.exception.ConflictException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateUserRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateUserRoleRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateUserStatusRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.UserResponse;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;
import ra.quan_ly_khoa_hoc.model.entity.User;
import ra.quan_ly_khoa_hoc.repository.UserRepository;
import ra.quan_ly_khoa_hoc.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if (userRepository.findByUsername(createUserRequest.getUsername()).isPresent()) {
            throw new ConflictException("Tài khoản " + createUserRequest.getUsername() + " đã tồn tại!");
        }
        if (userRepository.findByEmail(createUserRequest.getEmail()).isPresent()) {
            throw new ConflictException("Email " + createUserRequest.getEmail() + " đã tồn tại!");
        }
        User user = User.builder()
                .username(createUserRequest.getUsername())
                .password(passwordEncoder.encode(createUserRequest.getPassword()))
                .email(createUserRequest.getEmail())
                .fullName(createUserRequest.getFullName())
                .role(createUserRequest.getRole())
                .build();
        User savedUser = userRepository.save(user);
        return UserResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .isActive(savedUser.getIsActive())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Override
    public List<UserResponse> getAllUsers(RoleStatus role, Boolean status) {
        List<User> users;
        if (role != null && status != null) {
            users = userRepository.findByRoleAndIsActiveAndIsDeletedFalse(role, status);
        }
        else if (role != null) {
            users = userRepository.findByRoleAndIsDeletedFalse(role);
        }
        else if (status != null) {
            users = userRepository.findByIsActiveAndIsDeletedFalse(status);
        }
        else {
            users = userRepository.findByIsDeletedFalse();
        }
        return users.stream().map(user -> UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build()
        ).toList();
    }

    @Override
    public UserResponse getUserById(Integer id) {
        User user = userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại tài khoản có id " + id));
        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    public UserResponse updateUserRole(Integer targetId, UpdateUserRoleRequest role, Integer currentUserId) {
        User user = userRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại tài khoản có id " + targetId));
        if (user.getIsDeleted()) {
            throw new BadRequestException("Tài khoản này đã được xóa!");
        }
        if (user.getRole() == RoleStatus.ADMIN && !user.getId().equals(currentUserId)) {
            throw new BadRequestException("Không được thay đổi role của admin khác!");
        }
        user.setRole(role.getRole());
        User savedUser = userRepository.save(user);
        return UserResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .isActive(savedUser.getIsActive())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Override
    public UserResponse updateUserStatus(Integer targetId, UpdateUserStatusRequest status, Integer currentId) {
        User user = userRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại tài khoản có id " + targetId));
        if (user.getIsDeleted()) {
            throw new BadRequestException("Tài khoản này đã được xóa!");
        }
        if (user.getRole() == RoleStatus.ADMIN && !user.getId().equals(currentId)) {
            throw new BadRequestException("Không được thay đổi trạng thái hoạt động của admin khác!");
        }
        user.setIsActive(status.getStatus());
        User savedUser = userRepository.save(user);
        return UserResponse.builder()
                .userId(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole())
                .isActive(savedUser.getIsActive())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    @Override
    public void deleteUser(Integer targetId, Integer currentId) {
        User user = userRepository.findByIdAndIsDeletedFalse(targetId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại tài khoản có id " + targetId));
        if (user.getIsDeleted()) {
            throw new BadRequestException("Tài khoản này đã được xóa!");
        }
        if (user.getRole() == RoleStatus.ADMIN && !user.getId().equals(currentId)) {
            throw new BadRequestException("Không được xóa tài khoản admin khác!");
        }
        user.setFullName("Tài khoản vô danh");
        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
    }
}
