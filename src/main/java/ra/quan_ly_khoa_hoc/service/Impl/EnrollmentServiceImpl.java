package ra.quan_ly_khoa_hoc.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.quan_ly_khoa_hoc.exception.ConflictException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateEnrollmentRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.EnrollmentResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.LessonProgressResponse;
import ra.quan_ly_khoa_hoc.model.entity.*;
import ra.quan_ly_khoa_hoc.repository.CourseRepository;
import ra.quan_ly_khoa_hoc.repository.EnrollmentRepository;
import ra.quan_ly_khoa_hoc.repository.LessonProgressRepository;
import ra.quan_ly_khoa_hoc.repository.LessonRepository;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.EnrollmentService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final LessonRepository lessonRepository;

    @Override
    public List<EnrollmentResponse> getAllMyEnrollments() {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        List<Enrollment> enrolled = enrollmentRepository.findAllByStudentIdAndStatus(customUserDetails.getUser().getId(), EnrollmentStatus.ENROLLED);
        List<Enrollment> completed = enrollmentRepository.findAllByStudentIdAndStatus(customUserDetails.getUser().getId(), EnrollmentStatus.COMPLETED);
        List<Enrollment> enrollments = Stream.concat(enrolled.stream(), completed.stream()).toList();
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
                        .lessonProgresses(null)
                        .build()
                ).toList();
    }

    @Override
    @Transactional
    public EnrollmentResponse createEnrollment(CreateEnrollmentRequest createEnrollmentRequest) {
        Course course = courseRepository.findByIdAndIsDeletedFalse(createEnrollmentRequest.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + createEnrollmentRequest.getCourseId()));
        if (!course.getStatus().equals(CourseStatus.PUBLISHED)) {
            throw new ResourceNotFoundException("Không tồn tại khóa học có id " + createEnrollmentRequest.getCourseId());
        }
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student = customUserDetails.getUser();
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), createEnrollmentRequest.getCourseId())) {
            throw new ConflictException("Khóa học có id " + createEnrollmentRequest.getCourseId() + " đã được đăng ký!");
        }
        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .progressPercentage(BigDecimal.ZERO)
                .build();
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        List<Lesson> publishedLessons = lessonRepository.findByCourseIdAndIsPublishedTrueOrderByOrderIndex(course.getId());
        List<LessonProgress> lessonProgresses = List.of();
        if (!publishedLessons.isEmpty()) {
            lessonProgresses = publishedLessons.stream()
                    .map(lesson -> LessonProgress.builder()
                            .enrollment(savedEnrollment)
                            .lesson(lesson)
                            .isCompleted(false)
                            .completedAt(null)
                            .lastAccessedAt(LocalDateTime.now())
                            .build()
                    ).toList();
            lessonProgressRepository.saveAll(lessonProgresses);
        }
        List<LessonProgressResponse> progresses = lessonProgresses.stream()
                .map(lessonProgress -> LessonProgressResponse.builder()
                        .lessonId(lessonProgress.getLesson().getId())
                        .lessonName(lessonProgress.getLesson().getTitle())
                        .orderIndex(lessonProgress.getLesson().getOrderIndex())
                        .isCompleted(lessonProgress.getIsCompleted())
                        .completedAt(lessonProgress.getCompletedAt())
                        .lastAccessedAt(lessonProgress.getLastAccessedAt())
                        .build()
                ).toList();
        return EnrollmentResponse.builder()
                .enrollmentId(savedEnrollment.getId())
                .courseId(savedEnrollment.getCourse().getId())
                .courseTitle(savedEnrollment.getCourse().getTitle())
                .studentId(savedEnrollment.getStudent().getId())
                .studentName(savedEnrollment.getStudent().getFullName())
                .status(savedEnrollment.getStatus())
                .progressPercentage(savedEnrollment.getProgressPercentage())
                .enrollmentDate(savedEnrollment.getEnrollmentDate())
                .completionDate(null)
                .lessonProgresses(progresses)
                .build();
    }

    @Override
    public EnrollmentResponse getEnrollmentById(Integer enrollmentId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student = customUserDetails.getUser();
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentId(enrollmentId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đăng ký có id " + enrollmentId));
        List<LessonProgressResponse> lessonProgresses = enrollment.getLessonProgress()
                .stream()
                .map(lessonProgress -> LessonProgressResponse.builder()
                        .lessonId(lessonProgress.getLesson().getId())
                        .lessonName(lessonProgress.getLesson().getTitle())
                        .orderIndex(lessonProgress.getLesson().getOrderIndex())
                        .isCompleted(lessonProgress.getIsCompleted())
                        .completedAt(lessonProgress.getCompletedAt())
                        .lastAccessedAt(lessonProgress.getLastAccessedAt())
                        .build()
                ).toList();
        return EnrollmentResponse.builder()
                .enrollmentId(enrollmentId)
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .studentId(enrollment.getStudent().getId())
                .studentName(enrollment.getStudent().getFullName())
                .status(enrollment.getStatus())
                .progressPercentage(enrollment.getProgressPercentage())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .completionDate(enrollment.getCompletionDate())
                .lessonProgresses(lessonProgresses)
                .build();
    }

    @Override
    public EnrollmentResponse updateEnrollmentByEnrollmentIdAndLessonId(Integer enrollmentId, Integer lessonId) {
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User student = customUserDetails.getUser();
        Enrollment enrollment = enrollmentRepository.findByIdAndStudentId(enrollmentId, student.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu đăng ký có id " + enrollmentId));
        LessonProgress lessonProgress = lessonProgressRepository.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId));
        if (lessonProgress.getIsCompleted().equals(true)) {
            throw new ConflictException("Bài học có id " + lessonId + " đã hoàn thành!");
        }
        lessonProgress.setIsCompleted(true);
        lessonProgress.setCompletedAt(LocalDateTime.now());
        lessonProgressRepository.save(lessonProgress);
        Long countLessons = enrollmentRepository.countALLLessonProgressesByEnrollmentId(enrollmentId);
        Long countLessonsCompleted = enrollmentRepository.countAllLessonProgressesCompletedByEnrollmentId(enrollmentId);
        if (countLessons != null && countLessons > 0) {
            BigDecimal progressPercentage = BigDecimal.valueOf(countLessonsCompleted)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(countLessons), 2, RoundingMode.HALF_UP);
            enrollment.setProgressPercentage(progressPercentage);
        }
        else {
            enrollment.setProgressPercentage(BigDecimal.ZERO);
        }
        if (enrollment.getProgressPercentage().compareTo(BigDecimal.valueOf(100)) == 0) {
            enrollment.setCompletionDate(LocalDateTime.now());
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
        }
        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return EnrollmentResponse.builder()
                .enrollmentId(savedEnrollment.getId())
                .courseId(savedEnrollment.getCourse().getId())
                .courseTitle(savedEnrollment.getCourse().getTitle())
                .studentId(savedEnrollment.getStudent().getId())
                .studentName(savedEnrollment.getStudent().getFullName())
                .status(savedEnrollment.getStatus())
                .progressPercentage(savedEnrollment.getProgressPercentage())
                .enrollmentDate(savedEnrollment.getEnrollmentDate())
                .completionDate(savedEnrollment.getCompletionDate())
                .lessonProgresses(null)
                .build();
    }
}
