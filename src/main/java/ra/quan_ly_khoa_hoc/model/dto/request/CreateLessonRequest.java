package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateLessonRequest {
    @NotBlank(message = "Không được để trống tiêu đề!")
    private String title;
    private MultipartFile file;
    private String textContent;
    @NotNull(message = "Không được để trống thứ tự bài học!")
    private Integer orderIndex;
}
