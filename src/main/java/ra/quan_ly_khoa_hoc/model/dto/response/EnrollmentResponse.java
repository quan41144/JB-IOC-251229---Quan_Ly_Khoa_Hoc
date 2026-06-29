package ra.quan_ly_khoa_hoc.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ra.quan_ly_khoa_hoc.model.entity.EnrollmentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EnrollmentResponse {
    private Integer enrollmentId;
    private Integer courseId;
    private String courseTitle;
    private Integer studentId;
    private String studentName;
    private EnrollmentStatus status;
    private BigDecimal progressPercentage;
    private LocalDateTime enrollmentDate;
    private LocalDateTime completionDate;
    private List<LessonProgressResponse> lessonProgresses;
}
