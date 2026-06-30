package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UpdateReviewRequest {
    @Min(value = 1, message = "Không được bé hơn 1 sao!")
    @Max(value = 5, message = "Không được lớn hơn 5 sao!")
    private Integer rating;
    private String comment;
}
