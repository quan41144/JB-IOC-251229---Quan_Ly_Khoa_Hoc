package ra.quan_ly_khoa_hoc.service;

import org.apache.coyote.BadRequestException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateUserRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateUserRoleRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateUserStatusRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.UserResponse;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;
import ra.quan_ly_khoa_hoc.model.entity.User;

import java.util.List;

public interface UserService {
    UserResponse createUser(CreateUserRequest createUserRequest);
    List<UserResponse> getAllUsers(RoleStatus role, Boolean status);
    UserResponse getUserById(Integer id);
    UserResponse updateUserRole(Integer targetId, UpdateUserRoleRequest role, Integer currentUserId) throws BadRequestException;
    UserResponse updateUserStatus(Integer targetId, UpdateUserStatusRequest status, Integer currentUserId) throws BadRequestException;
    void deleteUser(Integer targetId, Integer currentUserId) throws BadRequestException;
}
