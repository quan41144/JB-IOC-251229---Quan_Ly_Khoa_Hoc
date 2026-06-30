package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateUserRequest {
    @NotBlank(message = "Không được để trống username!")
    private String username;
    @NotBlank(message = "Không được để trống password!")
    private String password;
    @NotBlank(message = "Không được để trống email!")
    @Email(message = "Sai định dạng của email!")
    private String email;
    @NotBlank(message = "Không được để trống họ và tên!")
    private String fullName;
    @NotNull(message = "Không được để trống quyền!")
    private RoleStatus role;
}
