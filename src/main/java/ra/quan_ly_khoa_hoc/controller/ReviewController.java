package ra.quan_ly_khoa_hoc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateReviewRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;
import ra.quan_ly_khoa_hoc.service.ReviewService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PutMapping("/{review_id}")
    public ResponseEntity<ApiResponse<?>> updateReview(@Valid @PathVariable("review_id") Integer reviewId, @Valid @RequestBody(required = false) UpdateReviewRequest updateReviewRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật bài đánh giá thành công!",
                reviewService.updateReview(reviewId, updateReviewRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @DeleteMapping("/{review_id}")
    public ResponseEntity<ApiResponse<?>> deleteReview(@Valid @PathVariable("review_id") Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa bài đánh giá thành công!",
                null,
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
