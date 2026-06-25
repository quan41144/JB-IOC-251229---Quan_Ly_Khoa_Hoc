package ra.quan_ly_khoa_hoc.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.quan_ly_khoa_hoc.model.entity.CourseStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CourseResponse {
    private Integer courseId;
    private String title;
    private String description;
    private Integer teacherId;
    private String teacherName;
    private BigDecimal price;
    private Integer duration;
    private CourseStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LessonResponse> lessons;
}
