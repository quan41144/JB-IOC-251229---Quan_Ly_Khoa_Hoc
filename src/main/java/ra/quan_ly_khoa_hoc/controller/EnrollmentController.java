package ra.quan_ly_khoa_hoc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateEnrollmentRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;
import ra.quan_ly_khoa_hoc.service.EnrollmentService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    private final EnrollmentService enrollmentService;
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllMyEnrollments() {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách các khóa học đã đăng ký thành công!",
                enrollmentService.getAllMyEnrollments(),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createEnrollment(@Valid @RequestBody CreateEnrollmentRequest createEnrollmentRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đăng ký khóa học thành công!",
                enrollmentService.createEnrollment(createEnrollmentRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }
    @GetMapping("/{enrollment_id}")
    public ResponseEntity<ApiResponse<?>> getEnrollmentById(@PathVariable("enrollment_id") Integer enrollmentId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy chi tiết thông tin đăng ký (tiến độ học) của mình thành công!",
                enrollmentService.getEnrollmentById(enrollmentId),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PutMapping("/{enrollment_id}/complete_lesson/{lesson_id}")
    public ResponseEntity<ApiResponse<?>> updateEnrollment(@Valid @PathVariable("enrollment_id") Integer enrollmentId, @Valid @PathVariable("lesson_id") Integer lessonId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật tiến độ học thành công!",
                enrollmentService.updateEnrollmentByEnrollmentIdAndLessonId(enrollmentId, lessonId),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
