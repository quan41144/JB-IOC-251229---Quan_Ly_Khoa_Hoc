package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateEnrollmentRequest {
    @NotNull(message = "Không được để trống id khóa học cần đăng ký!")
    private Integer courseId;
}
