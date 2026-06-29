package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserPasswordRequest {
    @NotBlank(message = "Vui lòng nhập lại mật khẩu hiện tại")
    private String oldPassword;
    @NotBlank(message = "Mật khẩu mới không được để trống!")
    private String newPassword;
}
