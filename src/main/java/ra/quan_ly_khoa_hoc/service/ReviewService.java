package ra.quan_ly_khoa_hoc.service;

import ra.quan_ly_khoa_hoc.model.dto.request.CreateReviewRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateReviewRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> getAllReviewsByCourseId(Integer courseId);
    ReviewResponse createReview(Integer courseId, CreateReviewRequest createReviewRequest);
    ReviewResponse updateReview(Integer reviewId, UpdateReviewRequest updateReviewRequest);
    void deleteReview(Integer reviewId);
}
