package ra.quan_ly_khoa_hoc.service;

import ra.quan_ly_khoa_hoc.model.dto.request.LoginRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.LoginResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.UserResponse;

public interface AuthService {
    LoginResponse login(LoginRequest loginRequest);
    UserResponse getMe(String username);
    Boolean verifyToken(String token);
}
