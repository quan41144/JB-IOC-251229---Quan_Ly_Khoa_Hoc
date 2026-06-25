package ra.quan_ly_khoa_hoc.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserResponse {
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private RoleStatus role;
    private Boolean isActive;
    private LocalDateTime createdAt;
}
