package ra.quan_ly_khoa_hoc.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "Failed");
        body.put("path", request.getServletPath());
        String message = (String) request.getAttribute("exception");
        if ("EXPIRED_TOKEN".equals(message)) {
            body.put("message", "Token đã hết hạn!");
            body.put("code", HttpStatus.UNAUTHORIZED);
        }
        else if ("INVALID_TOKEN_SIGNATURE".equals(message)) {
            body.put("message", "Token không hợp lệ!");
            body.put("code", HttpStatus.UNAUTHORIZED);
        }
        else if ("INVALID_TOKEN_MALFORMED".equals(message)) {
            body.put("message", "Token sai định dạng!");
            body.put("code", HttpStatus.UNAUTHORIZED);
        }
        else if ("INVALID_TOKEN_UNSUPPORTED".equals(message)) {
            body.put("message", "Token không được hỗ trợ!");
            body.put("code", HttpStatus.UNAUTHORIZED);
        }
        else if ("INVALID_TOKEN_ILLEGAL".equals(message)) {
            body.put("message", "Chuỗi token rỗng!");
            body.put("code", HttpStatus.UNAUTHORIZED);
        }
        else {
            body.put("message", "Lỗi xác thực token: " + message);
            body.put("code", HttpStatus.UNAUTHORIZED);
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
