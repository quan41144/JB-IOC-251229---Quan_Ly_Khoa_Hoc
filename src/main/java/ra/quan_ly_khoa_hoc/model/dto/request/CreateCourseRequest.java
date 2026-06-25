package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateCourseRequest {
    @NotBlank(message = "Không được để trống tiêu đề khóa học!")
    private String title;
    private String description;
    @NotNull(message = "Không được để trống giáo viên đứng lớp!")
    private Integer teacherId;
    private BigDecimal price = BigDecimal.ZERO;
    private Integer duration;
}
