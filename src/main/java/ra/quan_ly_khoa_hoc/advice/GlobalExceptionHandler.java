package ra.quan_ly_khoa_hoc.advice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ra.quan_ly_khoa_hoc.exception.AccessDeniedException;
import ra.quan_ly_khoa_hoc.exception.BadRequestException;
import ra.quan_ly_khoa_hoc.exception.ConflictException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Dữ liệu không hợp lệ!",
                null,
                errors,
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Sai kiểu dữ liệu trên URL!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequestException(BadRequestException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Lỗi nhập từ client!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Thông tin xác thực không hợp lệ!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Tài nguyên không tồn tại!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<?>> handleConflictException(ConflictException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Xung đột trạng thái tài nguyên!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.CONFLICT);
    }
    @ExceptionHandler({DisabledException.class, LockedException.class})
    public ResponseEntity<ApiResponse<?>> handleAccountStatusException(Exception ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Tài khoản của bạn đã bị khóa hoặc bị vô hiệu hóa!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Từ chối truy cập!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.FORBIDDEN);
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(new ApiResponse<>(
                false,
                "Định dạng dữ liệu không hợp lệ!",
                null,
                ex.getMessage(),
                LocalDateTime.now()
        ), HttpStatus.BAD_REQUEST);
    }
}
