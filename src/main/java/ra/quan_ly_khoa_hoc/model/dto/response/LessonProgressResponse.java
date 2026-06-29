package ra.quan_ly_khoa_hoc.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LessonProgressResponse {
    private Integer lessonId;
    private String lessonName;
    private Integer orderIndex;
    private Boolean isCompleted;
    private LocalDateTime completedAt;
    private LocalDateTime lastAccessedAt;
}
