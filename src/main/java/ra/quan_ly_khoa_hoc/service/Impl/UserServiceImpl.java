package ra.quan_ly_khoa_hoc.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.quan_ly_khoa_hoc.exception.BadRequestException;
import ra.quan_ly_khoa_hoc.exception.ConflictException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.*;
import ra.quan_ly_khoa_hoc.model.dto.response.UserResponse;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;
import ra.quan_ly_khoa_hoc.model.entity.User;
import ra.quan_ly_khoa_hoc.repository.UserRepository;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserResponse createUser(CreateUserRequest createUserRequest) {
        if (userRepository.findByUsernameAndIsDeletedFalse(createUserRequest.getUsername()).isPresent()) {
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
        if (user.getId().equals(currentUserId)) {
            throw new BadRequestException("Không được tự hạ quyền hoặc thay đổi vai trò của chính mình!");
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
        if (user.getId().equals(currentId)) {
            throw new BadRequestException("Không được tự khóa tài khoản của mình!");
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
        if (user.getId().equals(currentId)) {
            throw new BadRequestException("Không được tự xóa tài khoản của mình!");
        }
        if (user.getRole() == RoleStatus.ADMIN && !user.getId().equals(currentId)) {
            throw new BadRequestException("Không được xóa tài khoản admin khác!");
        }
        user.setFullName("Tài khoản vô danh");
        user.setIsDeleted(true);
        user.setIsActive(false);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Integer userId, UpdateUserRequest updateUserRequest) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại tài khoản có id " + userId));
        if (!user.getId().equals(customUserDetails.getUser().getId()) && customUserDetails.getUser().getRole() != RoleStatus.ADMIN) {
            throw new ResourceNotFoundException("Không tồn tại tài khoản có id " + userId);
        }
        if (updateUserRequest.getEmail() != null && !updateUserRequest.getEmail().isEmpty() && !user.getEmail().equals(updateUserRequest.getEmail())) {
            if (userRepository.findByEmail(updateUserRequest.getEmail()).isPresent()) {
                throw new ConflictException("Email " + updateUserRequest.getEmail() + " đã tồn tại!");
            }
            user.setEmail(updateUserRequest.getEmail());
        }
        if (updateUserRequest.getFullName() != null && !user.getFullName().equals(updateUserRequest.getFullName())) {
            user.setFullName(updateUserRequest.getFullName());
        }
        userRepository.save(user);
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
    @Transactional
    public void updateUserPassword(Integer userId, UpdateUserPasswordRequest updateUserPasswordRequest) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = userRepository.findByIdAndIsDeletedFalse(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại tài khoản có id " + userId));
        if (!passwordEncoder.matches(updateUserPasswordRequest.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu hiện tại không đúng!");
        }
        if (passwordEncoder.matches(updateUserPasswordRequest.getNewPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu mới không được giống mật khẩu cũ!");
        }
        user.setPassword(passwordEncoder.encode(updateUserPasswordRequest.getNewPassword()));
        userRepository.save(user);
    }
}
