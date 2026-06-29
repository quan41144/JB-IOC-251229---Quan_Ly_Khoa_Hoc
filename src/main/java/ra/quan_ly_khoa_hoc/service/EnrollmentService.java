package ra.quan_ly_khoa_hoc.service;

import ra.quan_ly_khoa_hoc.model.dto.request.CreateEnrollmentRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {
    List<EnrollmentResponse> getAllMyEnrollments();
    EnrollmentResponse createEnrollment(CreateEnrollmentRequest createEnrollmentRequest);
    EnrollmentResponse getEnrollmentById(Integer enrollmentId);
    EnrollmentResponse updateEnrollmentByEnrollmentIdAndLessonId(Integer enrollmentId, Integer lessonId);
}
