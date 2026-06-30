package ra.quan_ly_khoa_hoc.service;

import ra.quan_ly_khoa_hoc.model.dto.response.CourseResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.EnrollmentResponse;

import java.util.List;

public interface ReportService {
    List<CourseResponse> findTopCoursesByEnrollment();
    List<EnrollmentResponse> getAllEnrollmentsByStudentId(Integer studentId);
    List<CourseResponse> findAllCoursesByTeacherId(Integer teacherId);
}
