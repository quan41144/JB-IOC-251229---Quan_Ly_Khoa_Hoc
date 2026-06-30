package ra.quan_ly_khoa_hoc.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreateReviewRequest {
    @NotNull(message = "Không được để trống rating!")
    @Min(value = 1, message = "Không được bé hơn 1 sao!")
    @Max(value = 5, message = "Không được lớn hơn 5 sao!")
    private Integer rating;
    @NotBlank(message = "Không được để trống comment!")
    private String comment;
}
