package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateUserRequest {
    @Email(message = "Không đúng định dạng email!")
    private String email;
    private String fullName;
}
