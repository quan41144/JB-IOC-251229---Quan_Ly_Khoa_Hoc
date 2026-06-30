package ra.quan_ly_khoa_hoc.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ra.quan_ly_khoa_hoc.exception.BadRequestException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.response.CourseResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.EnrollmentResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.LessonProgressResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.LessonResponse;
import ra.quan_ly_khoa_hoc.model.entity.*;
import ra.quan_ly_khoa_hoc.repository.*;
import ra.quan_ly_khoa_hoc.service.ReportService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {
    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;

    @Override
    public List<CourseResponse> findTopCoursesByEnrollment() {
        List<Course> courses = reportRepository.findTopCoursesByEnrollment();
        return courses.stream()
                .map(c -> CourseResponse.builder()
                        .courseId(c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .teacherId(c.getTeacher().getId())
                        .teacherName(c.getTeacher().getFullName())
                        .price(c.getPrice())
                        .duration(c.getDuration())
                        .status(c.getStatus())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .lessons(null)
                        .build()
                ).limit(10).toList();
    }

    @Override
    public List<EnrollmentResponse> getAllEnrollmentsByStudentId(Integer studentId) {
        User student = userRepository.findByIdAndIsDeletedFalse(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại sinh viên có id " + studentId));
        if (student.getRole() != RoleStatus.STUDENT) {
            throw new BadRequestException("Đây không phải là học sinh!");
        }
        List<Enrollment> enrollments = enrollmentRepository.findAllByStudentId(studentId);
        return enrollments.stream()
                .map(enrollment -> EnrollmentResponse.builder()
                        .enrollmentId(enrollment.getId())
                        .courseId(enrollment.getCourse().getId())
                        .courseTitle(enrollment.getCourse().getTitle())
                        .studentId(enrollment.getStudent().getId())
                        .studentName(enrollment.getStudent().getFullName())
                        .status(enrollment.getStatus())
                        .progressPercentage(enrollment.getProgressPercentage())
                        .enrollmentDate(enrollment.getEnrollmentDate())
                        .completionDate(enrollment.getCompletionDate())
                        .lessonProgresses(lessonProgressRepository.findByEnrollmentId(enrollment.getId()).stream()
                                .map(lp -> LessonProgressResponse.builder()
                                        .lessonId(lp.getLesson().getId())
                                        .lessonName(lp.getLesson().getTitle())
                                        .orderIndex(lp.getLesson().getOrderIndex())
                                        .isCompleted(lp.getIsCompleted())
                                        .completedAt(lp.getCompletedAt())
                                        .lastAccessedAt(lp.getLastAccessedAt())
                                        .build()
                                ).toList()
                        )
                        .build()
                ).toList();
    }

    @Override
    public List<CourseResponse> findAllCoursesByTeacherId(Integer teacherId) {
        User teacher = userRepository.findByIdAndIsDeletedFalse(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại người dùng có id " + teacherId));
        if (teacher.getRole() != RoleStatus.TEACHER) {
            throw new BadRequestException("Đây không phải là giáo viên!");
        }
        List<Course> courses = courseRepository.findByTeacherIdAndIsDeletedFalse(teacherId);
        return courses.stream()
                .map(c -> CourseResponse.builder()
                        .courseId(c.getId())
                        .title(c.getTitle())
                        .description(c.getDescription())
                        .teacherId(c.getTeacher().getId())
                        .teacherName(c.getTeacher().getFullName())
                        .price(c.getPrice())
                        .duration(c.getDuration())
                        .status(c.getStatus())
                        .createdAt(c.getCreatedAt())
                        .updatedAt(c.getUpdatedAt())
                        .lessons(lessonRepository.findByCourseIdOrderByOrderIndex(c.getId()).stream()
                                .map(l -> LessonResponse.builder()
                                        .lessonId(l.getId())
                                        .courseId(l.getCourse().getId())
                                        .title(l.getTitle())
                                        .contentUrl(l.getContentURL())
                                        .textContent(l.getTextContent())
                                        .orderIndex(l.getOrderIndex())
                                        .isPublished(l.getIsPublished())
                                        .createdAt(l.getCreatedAt())
                                        .updatedAt(l.getUpdatedAt())
                                        .build()
                                ).toList()
                        )
                        .build()
                ).toList();
    }
}
