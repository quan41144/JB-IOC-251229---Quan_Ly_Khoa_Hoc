package ra.quan_ly_khoa_hoc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseStatusRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;
import ra.quan_ly_khoa_hoc.model.entity.CourseStatus;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.CourseService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllCourses(@Valid Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách tất cả khóa học thành công!",
                courseService.getAllCourses(customUserDetails.getUser().getRole()),
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
    public ResponseEntity<ApiResponse<?>> createCourse(@Valid @RequestBody CreateCourseRequest createCourseRequest) throws BadRequestException {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Tạo khóa học mới thành công!",
                courseService.createCourse(createCourseRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }
    @PutMapping("/{course_id}")
    public ResponseEntity<ApiResponse<?>> updateCourse(@Valid @PathVariable Integer course_id, @Valid @RequestBody UpdateCourseRequest updateCourseRequest) throws BadRequestException {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật thông tin chi tiết khóa học có id " + course_id + " thành công!",
                courseService.updateCourse(course_id, updateCourseRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PutMapping("/{course_id}/status")
    public ResponseEntity<ApiResponse<?>> updateCourseStatus(@Valid @PathVariable Integer course_id, @Valid @RequestBody UpdateCourseStatusRequest updateCourseStatusRequest) throws BadRequestException {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật trạng thái khóa học thành công!",
                courseService.updateCourseStatus(course_id, updateCourseStatusRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @DeleteMapping("/{course_id}")
    public ResponseEntity<ApiResponse<?>> deleteCourse(@Valid @PathVariable Integer course_id) throws BadRequestException {
        courseService.deleteCourse(course_id);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa khóa học thành công!",
                null,
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
