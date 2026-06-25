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
public class LoginRequest {
    @NotBlank(message = "Không được để trống username!")
    private String username;
    @NotBlank(message = "Không được để trống password!")
    private String password;
}
