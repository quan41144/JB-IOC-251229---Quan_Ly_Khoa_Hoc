package ra.quan_ly_khoa_hoc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateUserRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateUserRoleRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateUserStatusRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.UserService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllUsers(@Valid @RequestParam(required = false) RoleStatus role, @Valid @RequestParam(required = false) Boolean status) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách thành công!",
                userService.getAllUsers(role, status),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponse<?>> getUserById(@Valid @PathVariable Integer user_id) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy thông tin chi tiết người dùng thành công!",
                userService.getUserById(user_id),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Tạo tài khoản người dùng mới thành công!",
                userService.createUser(createUserRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }
    @PutMapping("/{user_id}/role")
    public ResponseEntity<ApiResponse<?>> updateUserRole(@Valid @PathVariable Integer user_id, @Valid @RequestBody UpdateUserRoleRequest role, @Valid Authentication authentication) throws BadRequestException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật vai trò người dùng thành công!",
                userService.updateUserRole(user_id, role, customUserDetails.getUser().getId()),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PutMapping("/{user_id}/status")
    public ResponseEntity<ApiResponse<?>> updateUserStatus(@Valid @PathVariable Integer user_id, @Valid @RequestBody UpdateUserStatusRequest status, @Valid Authentication authentication) throws BadRequestException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhập trạng thái của người dùng thành công!",
                userService.updateUserStatus(user_id, status, customUserDetails.getUser().getId()),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @DeleteMapping("/{user_id}")
    public ResponseEntity<ApiResponse<?>> deleteUser(@Valid @PathVariable Integer user_id, @Valid Authentication authentication) throws BadRequestException {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        userService.deleteUser(user_id, customUserDetails.getUser().getId());
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa người dùng có id " + user_id + " khỏi hệ thống thành công!",
                null,
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
