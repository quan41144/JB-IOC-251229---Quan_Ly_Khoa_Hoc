package ra.quan_ly_khoa_hoc.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ra.quan_ly_khoa_hoc.model.dto.request.LoginRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;
import ra.quan_ly_khoa_hoc.service.AuthService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody LoginRequest loginRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đăng nhập thành công!",
                authService.login(loginRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }

    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<?>> verify(@Valid HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xác thực token người dùng thành công!",
                authService.verifyToken(token),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<?>> getMe(Authentication authentication) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy thông tin hồ sơ người dùng thành công!",
                authService.getMe(authentication.getName()),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
