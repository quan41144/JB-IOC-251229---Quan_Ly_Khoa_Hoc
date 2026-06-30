package ra.quan_ly_khoa_hoc.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;
import ra.quan_ly_khoa_hoc.service.ReportService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/top_courses")
    public ResponseEntity<ApiResponse<?>> getTopCourses() {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách khóa học phổ biến nhất thành công!",
                reportService.findTopCoursesByEnrollment(),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @GetMapping("/student_progress/{student_id}")
    public ResponseEntity<ApiResponse<?>> getStudentProgress(@PathVariable("student_id") Integer studentId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thống kê tiến độ học của một sinh viên cụ thể thành công!",
                reportService.getAllEnrollmentsByStudentId(studentId),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @GetMapping("/teacher_courses_overview/{teacher_id}")
    public ResponseEntity<ApiResponse<?>> getTeacherCoursesOverview(@PathVariable("teacher_id") Integer teacherId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Thống kê tổng quan về các khóa học của giảng viên thành công!",
                reportService.findAllCoursesByTeacherId(teacherId),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
