package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateNotificationRequest {
    @NotNull(message = "Không được bỏ trống user_id!")
    private Integer userId;
    @NotBlank(message = "Không được bỏ trống tin nhắn!")
    private String message;
    private String type;
    private String targetUrl;
}
