package ra.quan_ly_khoa_hoc.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ContentPreviewResponse {
    private Integer courseId;
    private String courseName;
    private String lessonName;
    private String textContent;
}
