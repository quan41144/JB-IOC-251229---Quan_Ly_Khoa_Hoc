package ra.quan_ly_khoa_hoc.security.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ra.quan_ly_khoa_hoc.model.entity.InvalidateToken;
import ra.quan_ly_khoa_hoc.model.entity.User;
import ra.quan_ly_khoa_hoc.repository.InvalidateTokenRepository;
import ra.quan_ly_khoa_hoc.repository.UserRepository;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;
    private final UserDetailsService userDetailsService;
    private final InvalidateTokenRepository invalidateTokenRepository;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);
        if (token != null && !token.trim().isEmpty()) {
            if (invalidateTokenRepository.existsByToken(token)) {
                request.setAttribute("exception", "INVALID_TOKEN_REVOKED");
                filterChain.doFilter(request, response);
                return;
            }
            try {
                if (jwtProvider.validateToken(token)) {
                    String username = jwtProvider.getUsernameFromToken(token);
                    User user = userRepository.findByUsernameAndIsDeletedFalse(username).orElse(null);
                    if (user != null && !user.getIsActive()) {
                        LocalDateTime expiryDate = jwtProvider.getExpirationDateFromToken(token);
                        InvalidateToken invalidateToken = InvalidateToken.builder()
                                .token(token)
                                .expiryDate(expiryDate)
                                .build();
                        invalidateTokenRepository.save(invalidateToken);
                        request.setAttribute("exception", "INVALID_TOKEN_REVOKED");
                        filterChain.doFilter(request, response);
                        return;
                    }
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                }
            }
            catch (MalformedJwtException e) {
                request.setAttribute("exception", "INVALID_TOKEN_MALFORMED");
            }
            catch (ExpiredJwtException e) {
                request.setAttribute("exception", "EXPIRED_TOKEN");
            }
            catch (UnsupportedJwtException e) {
                request.setAttribute("exception", "INVALID_TOKEN_UNSUPPORTED");
            }
            catch (IllegalArgumentException e) {
                request.setAttribute("exception", "INVALID_TOKEN_ILLEGAL");
            }
            catch (SignatureException e) {
                request.setAttribute("exception", "INVALID_TOKEN_SIGNATURE");
            }
        }
        filterChain.doFilter(request, response);
    }
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
