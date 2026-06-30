package ra.quan_ly_khoa_hoc.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ra.quan_ly_khoa_hoc.exception.BadRequestException;
import ra.quan_ly_khoa_hoc.exception.ConflictException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateReviewRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateReviewRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ReviewResponse;
import ra.quan_ly_khoa_hoc.model.entity.*;
import ra.quan_ly_khoa_hoc.repository.CourseRepository;
import ra.quan_ly_khoa_hoc.repository.EnrollmentRepository;
import ra.quan_ly_khoa_hoc.repository.ReviewRepository;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.ReviewService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<ReviewResponse> getAllReviewsByCourseId(Integer courseId) {
        if (courseRepository.findByIdAndIsDeletedFalse(courseId).isEmpty()) {
                throw new ResourceNotFoundException("Không tồn tại khóa học có id " +  courseId);
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User user = customUserDetails.getUser();
        Course course = courseRepository.findByIdAndIsDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " +  courseId));
        if (course.getStatus().equals(CourseStatus.DRAFT) && !user.getRole().equals(RoleStatus.ADMIN)) {
            throw new ResourceNotFoundException("Không tồn tại khóa học có id " +  courseId);
        }
        if (course.getStatus().equals(CourseStatus.ARCHIVED)) {
            if (user.getRole().equals(RoleStatus.STUDENT)) {
                if (!enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), courseId)) {
                    throw new ResourceNotFoundException("Không tồn tại khóa học có id " +  courseId);
                }
            }
            else if (user.getRole().equals(RoleStatus.TEACHER)) {
                if (!course.getTeacher().getId().equals(user.getId())) {
                    throw new ResourceNotFoundException("Không tồn tại khóa học có id " +  courseId);
                }
            }
        }
        List<Review> reviews = reviewRepository.findReviewsByCourseId(courseId);
        return reviews.stream()
                .map(r -> ReviewResponse.builder()
                        .reviewId(r.getId())
                        .courseName(r.getCourse().getTitle())
                        .studentName(r.getStudent().getFullName())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .updatedAt(r.getUpdatedAt())
                        .build()
                ).toList();
    }

    @Override
    public ReviewResponse createReview(Integer courseId, CreateReviewRequest createReviewRequest) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Course course = courseRepository.findByIdAndIsDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + courseId));
        User student = customUserDetails.getUser();
        if (!student.getRole().equals(RoleStatus.STUDENT)) {
            throw new BadRequestException("Chỉ học sinh mới được quyền đánh giá/bình luận về khóa học!");
        }
        if (reviewRepository.findByCourseIdAndStudentId(courseId, student.getId()).isPresent()) {
            throw new ConflictException("Bạn đã đánh giá khóa học này rồi!");
        }
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new BadRequestException("Bạn phải đăng ký khóa học trước khi đánh giá!");
        }
        Review review = Review.builder()
                .course(course)
                .student(student)
                .rating(createReviewRequest.getRating())
                .comment(createReviewRequest.getComment())
                .build();
        reviewRepository.save(review);
        return ReviewResponse.builder()
                .reviewId(review.getId())
                .courseName(review.getCourse().getTitle())
                .studentName(review.getStudent().getFullName())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .build();
    }

    @Override
    public ReviewResponse updateReview(Integer reviewId, UpdateReviewRequest updateReviewRequest) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài đánh giá có id " +  reviewId));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User user = customUserDetails.getUser();
        if (!user.getId().equals(review.getStudent().getId()) && !user.getRole().equals(RoleStatus.ADMIN)) {
            throw new BadRequestException("Bạn không có quyền sửa!");
        }
        if (updateReviewRequest.getRating() != null) {
            review.setRating(updateReviewRequest.getRating());
        }
        if (updateReviewRequest.getComment() != null) {
            review.setComment(updateReviewRequest.getComment());
        }
        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.builder()
                .reviewId(savedReview.getId())
                .courseName(savedReview.getCourse().getTitle())
                .studentName(savedReview.getStudent().getFullName())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .createdAt(savedReview.getCreatedAt())
                .updatedAt(savedReview.getUpdatedAt())
                .build();
    }

    @Override
    public void deleteReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài đánh giá có id " +  reviewId));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User user = customUserDetails.getUser();
        if (!user.getId().equals(review.getStudent().getId()) && !user.getRole().equals(RoleStatus.ADMIN)) {
            throw new BadRequestException("Bạn không có quyền xóa!");
        }
        reviewRepository.delete(review);
    }
}
