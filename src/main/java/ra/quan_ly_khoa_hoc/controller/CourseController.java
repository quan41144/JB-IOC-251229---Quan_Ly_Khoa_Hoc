package ra.quan_ly_khoa_hoc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ra.quan_ly_khoa_hoc.model.dto.request.*;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;
import ra.quan_ly_khoa_hoc.model.entity.CourseStatus;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.CourseService;
import ra.quan_ly_khoa_hoc.service.LessonService;
import ra.quan_ly_khoa_hoc.service.ReviewService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;
    private final LessonService lessonService;
    private final ReviewService reviewService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCourses(@Valid @RequestParam(value = "search", required = false) String keyword, @Valid @RequestParam(value = "teacher_id", required = false) Integer teacherId, @Valid @RequestParam(value = "status", required = false)CourseStatus status) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách tất cả khóa học thành công!",
                courseService.getAllCourses(keyword, teacherId, status),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @GetMapping("/{course_id}")
    public ResponseEntity<ApiResponse<?>> getCourseById(@Valid @PathVariable Integer course_id) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy thông tin chi tiết khóa học id " + course_id + " thành công!",
                courseService.getCourseById(course_id),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Tạo khóa học mới thành công!",
                courseService.createCourse(createCourseRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }
    @PutMapping("/{course_id}")
    public ResponseEntity<ApiResponse<?>> updateCourse(@Valid @PathVariable Integer course_id, @Valid @RequestBody UpdateCourseRequest updateCourseRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật thông tin chi tiết khóa học có id " + course_id + " thành công!",
                courseService.updateCourse(course_id, updateCourseRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PutMapping("/{course_id}/status")
    public ResponseEntity<ApiResponse<?>> updateCourseStatus(@Valid @PathVariable Integer course_id, @Valid @RequestBody UpdateCourseStatusRequest updateCourseStatusRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật trạng thái khóa học thành công!",
                courseService.updateCourseStatus(course_id, updateCourseStatusRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @DeleteMapping("/{course_id}")
    public ResponseEntity<ApiResponse<?>> deleteCourse(@Valid @PathVariable Integer course_id) {
        courseService.deleteCourse(course_id);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa khóa học thành công!",
                null,
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @GetMapping("/{course_id}/lessons")
    public ResponseEntity<ApiResponse<?>> getAllLessons(@Valid @PathVariable Integer course_id) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách tất cả các bài học trong khóa học có id " + course_id + " thành công!",
                lessonService.getAllLessons(course_id),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PostMapping("/{course_id}/lessons")
    public ResponseEntity<ApiResponse<?>> createLesson(@Valid @PathVariable("course_id") Integer courseId, @Valid @ModelAttribute CreateLessonRequest createLessonRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thêm mới bài học thành công!",
                lessonService.createLesson(courseId, createLessonRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }
    @GetMapping("/{course_id}/reviews")
    public ResponseEntity<ApiResponse<?>> getAllReviews(@Valid @PathVariable("course_id") Integer courseId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách đánh giá/bình luận về khóa học thành công!",
                reviewService.getAllReviewsByCourseId(courseId),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PostMapping("/{course_id}/reviews")
    public ResponseEntity<ApiResponse<?>> createReview(@Valid @PathVariable("course_id") Integer courseId, @Valid @RequestBody CreateReviewRequest createReviewRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Tạo bài đánh giá/bình luận về khóa học thành công!",
                reviewService.createReview(courseId, createReviewRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }
}
