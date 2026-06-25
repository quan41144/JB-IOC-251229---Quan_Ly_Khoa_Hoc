package ra.quan_ly_khoa_hoc.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ApiResponse <T>{
    Boolean success;
    String message;
    T data;
    T error;
    LocalDateTime timestamp;
}
