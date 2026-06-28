package ra.quan_ly_khoa_hoc.service;

import ra.quan_ly_khoa_hoc.model.dto.request.CreateUserRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateUserRoleRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateUserStatusRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.UserResponse;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest createUserRequest);
    List<UserResponse> getAllUsers(RoleStatus role, Boolean status);
    UserResponse getUserById(Integer id);
    UserResponse updateUserRole(Integer targetId, UpdateUserRoleRequest role, Integer currentUserId);
    UserResponse updateUserStatus(Integer targetId, UpdateUserStatusRequest status, Integer currentUserId);
    void deleteUser(Integer targetId, Integer currentUserId);
}
