package ra.quan_ly_khoa_hoc.service.Impl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.LoginRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.LoginResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.UserResponse;
import ra.quan_ly_khoa_hoc.model.entity.InvalidateToken;
import ra.quan_ly_khoa_hoc.model.entity.User;
import ra.quan_ly_khoa_hoc.repository.InvalidateTokenRepository;
import ra.quan_ly_khoa_hoc.repository.UserRepository;
import ra.quan_ly_khoa_hoc.security.jwt.JwtProvider;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.AuthService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final InvalidateTokenRepository invalidateTokenRepository;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        String accessToken = jwtProvider.generateAccessToken(userDetails.getUser().getUsername());
        String refreshToken = jwtProvider.generateRefreshToken(userDetails.getUser().getUsername());
        return new LoginResponse(accessToken, refreshToken);
    }

    @Override
    public UserResponse getMe(String username) {
        User user = userRepository.findByUsernameAndIsDeletedFalse(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy user!"));
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
    public Boolean verifyToken(String token) {
        try {
            return jwtProvider.validateToken(token);
        }
        catch (Exception e) {
            return false;
        }
    }

    @Override
    @Transactional
    public void logout() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                LocalDateTime expiryDate = jwtProvider.getExpirationDateFromToken(token);
                if (!invalidateTokenRepository.existsByToken(token)) {
                    InvalidateToken invalidateToken = InvalidateToken.builder()
                            .token(token)
                            .expiryDate(expiryDate)
                            .build();
                    invalidateTokenRepository.save(invalidateToken);
                }
            }
        }
        SecurityContextHolder.clearContext();
    }
}
