package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserRoleRequest {
    @NotNull(message = "Không được để trống quyền!")
    private RoleStatus role;
}
