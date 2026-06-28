package ra.quan_ly_khoa_hoc.model.dto.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateLessonRequest {
    private String title;
    private MultipartFile file;
    private String textContent;
    private Integer orderIndex;
}
