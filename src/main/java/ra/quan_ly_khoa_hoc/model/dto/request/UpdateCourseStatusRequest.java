package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.quan_ly_khoa_hoc.model.entity.CourseStatus;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateCourseStatusRequest {
    @NotNull(message = "Không được để trống trạng thái của khóa học!")
    private CourseStatus status;
}
