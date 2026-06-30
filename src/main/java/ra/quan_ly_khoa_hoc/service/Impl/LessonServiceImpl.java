package ra.quan_ly_khoa_hoc.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ra.quan_ly_khoa_hoc.exception.BadRequestException;
import ra.quan_ly_khoa_hoc.exception.ConflictException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateLessonRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateLessonPublishRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateLessonRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ContentPreviewResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.LessonResponse;
import ra.quan_ly_khoa_hoc.model.entity.*;
import ra.quan_ly_khoa_hoc.repository.CourseRepository;
import ra.quan_ly_khoa_hoc.repository.EnrollmentRepository;
import ra.quan_ly_khoa_hoc.repository.LessonProgressRepository;
import ra.quan_ly_khoa_hoc.repository.LessonRepository;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.LessonService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final LessonProgressRepository lessonProgressRepository;
    private final Cloudinary cloudinary;

    @Override
    public List<LessonResponse> getAllLessons(Integer courseId) {
        Course course = courseRepository.findByIdAndIsDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học có id " + courseId + " không tồn tại!"));
        boolean isStudent = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_STUDENT"));
        boolean isTeacher = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_TEACHER"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (course.getStatus() == CourseStatus.DRAFT) {
            if (isTeacher) {
                CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
                if (!course.getTeacher().getId().equals(customUserDetails.getUser().getId())) {
                    throw new ResourceNotFoundException("Khóa học có id " + courseId + " không tồn tại!");
                }
            }
            else if (isStudent) {
                throw new ResourceNotFoundException("Khóa học có id " + courseId + " không tồn tại!");
            }
        }
        if (course.getStatus() == CourseStatus.ARCHIVED && isStudent) {
            CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
            if (!enrollmentRepository.existsByStudentIdAndCourseId(customUserDetails.getUser().getId(), course.getId())) {
                throw new ResourceNotFoundException("Khóa học có id " + courseId + " không tồn tại!");
            }
        }
        List<Lesson> lessons;
        if (isStudent) {
            lessons = lessonRepository.findByCourseIdAndIsPublishedTrueOrderByOrderIndex(course.getId());
        }
        else {
            lessons = lessonRepository.findByCourseIdOrderByOrderIndex(course.getId());
        }
        List<LessonResponse> lessonResponses = lessons.stream()
                .map(lesson -> LessonResponse.builder()
                        .lessonId(lesson.getId())
                        .courseId(courseId)
                        .title(lesson.getTitle())
                        .contentUrl(lesson.getContentURL())
                        .textContent(lesson.getTextContent())
                        .orderIndex(lesson.getOrderIndex())
                        .isPublished(lesson.getIsPublished())
                        .createdAt(lesson.getCreatedAt())
                        .updatedAt(lesson.getUpdatedAt())
                        .build()
                ).toList();
        return lessonResponses;
    }

    @Override
    public LessonResponse getLessonById(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId));
        Course course = courseRepository.findByIdAndIsDeletedFalse(lesson.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId));
        boolean isStudent = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_STUDENT"));
        boolean isTeacher = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_TEACHER"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (isStudent && !lesson.getIsPublished()) {
            throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
        }
        if (course.getStatus() == CourseStatus.DRAFT) {
            if (isTeacher) {
                CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
                if (!course.getTeacher().getId().equals(customUserDetails.getUser().getId())) {
                    throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
                }
            }
            else if (isStudent) {
                throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
            }
        }
        if (course.getStatus() == CourseStatus.ARCHIVED && isStudent) {
            CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
            if (!enrollmentRepository.existsByStudentIdAndCourseId(customUserDetails.getUser().getId(), course.getId())) {
                throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
            }
        }
        return LessonResponse.builder()
                .lessonId(lesson.getId())
                .courseId(lesson.getCourse().getId())
                .title(lesson.getTitle())
                .contentUrl(lesson.getContentURL())
                .textContent(lesson.getTextContent())
                .orderIndex(lesson.getOrderIndex())
                .isPublished(lesson.getIsPublished())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }

    @Override
    public LessonResponse createLesson(Integer courseId, CreateLessonRequest createLessonRequest) {
        Course course = courseRepository.findByIdAndIsDeletedFalse(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + courseId));
        if (lessonRepository.existsByCourseIdAndOrderIndex(courseId, createLessonRequest.getOrderIndex())) {
            throw new ConflictException("Thứ tự bài học " + createLessonRequest.getOrderIndex() + " đã tồn tại!");
        }
        MultipartFile file = createLessonRequest.getFile();
        String cloudinaryUrl = "";
        if (file != null && !file.isEmpty()) {
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
                cloudinaryUrl = uploadResult.get("secure_url").toString();
            } catch (Exception e) {
                throw new RuntimeException("Lỗi: " + e.getMessage());
            }
        }
        Lesson lesson = Lesson.builder()
                .course(course)
                .title(createLessonRequest.getTitle())
                .contentURL(cloudinaryUrl)
                .textContent(createLessonRequest.getTextContent())
                .orderIndex(createLessonRequest.getOrderIndex())
                .build();
        Lesson savedLesson = lessonRepository.save(lesson);
        return LessonResponse.builder()
                .lessonId(savedLesson.getId())
                .courseId(savedLesson.getCourse().getId())
                .title(savedLesson.getTitle())
                .contentUrl(savedLesson.getContentURL())
                .textContent(savedLesson.getTextContent())
                .orderIndex(savedLesson.getOrderIndex())
                .isPublished(savedLesson.getIsPublished())
                .createdAt(savedLesson.getCreatedAt())
                .updatedAt(savedLesson.getUpdatedAt())
                .build();
    }

    @Override
    public LessonResponse updateLesson(Integer lessonId, UpdateLessonRequest updateLessonRequest) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId));
        Course course = courseRepository.findByIdAndIsDeletedFalse(lesson.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId));
        if (updateLessonRequest.getTitle() != null) {
            lesson.setTitle(updateLessonRequest.getTitle());
        }
        if (updateLessonRequest.getTextContent() != null) {
            lesson.setTextContent(updateLessonRequest.getTextContent());
        }

        if (updateLessonRequest.getOrderIndex() != null && !lesson.getOrderIndex().equals(updateLessonRequest.getOrderIndex())) {
            if (lessonRepository.existsByCourseIdAndOrderIndexAndIdNot(lesson.getCourse().getId(), updateLessonRequest.getOrderIndex(), lessonId)) {
                throw new ConflictException("Thứ tự bài học " + updateLessonRequest.getOrderIndex() + " đã tồn tại trong khóa học này!");
            }
            lesson.setOrderIndex(updateLessonRequest.getOrderIndex());
        }
        MultipartFile file = updateLessonRequest.getFile();
        if (file != null && !file.isEmpty()) {
            String cloudinaryUrl = "";
            try {
                Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
                cloudinaryUrl = uploadResult.get("secure_url").toString();
                lesson.setContentURL(cloudinaryUrl);
            }
            catch (Exception e) {
                throw new RuntimeException("Lỗi: " + e.getMessage());
            }
        }
        Lesson savedLesson = lessonRepository.save(lesson);
        return LessonResponse.builder()
                .lessonId(savedLesson.getId())
                .courseId(savedLesson.getCourse().getId())
                .title(savedLesson.getTitle())
                .contentUrl(savedLesson.getContentURL())
                .textContent(savedLesson.getTextContent())
                .orderIndex(savedLesson.getOrderIndex())
                .isPublished(savedLesson.getIsPublished())
                .createdAt(savedLesson.getCreatedAt())
                .updatedAt(savedLesson.getUpdatedAt())
                .build();
    }

    @Override
    @Transactional
    public LessonResponse updateLessonPublish(Integer lessonId, UpdateLessonPublishRequest updateLessonPublishRequest) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId));
        Course course = courseRepository.findByIdAndIsDeletedFalse(lesson.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + lessonId));
        CustomUserDetails customUserDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (customUserDetails.getUser().getRole() == RoleStatus.TEACHER && !course.getTeacher().getId().equals(customUserDetails.getUser().getId())) {
            throw new BadRequestException("Bạn không có quyền chỉnh sửa khóa học này!");
        }
        Boolean publish = updateLessonPublishRequest.getPublish();
        if (!lesson.getIsPublished().equals(publish)) {
            if (publish) {
                List<Enrollment> enrollments = course.getEnrollments();
                if (enrollments != null && !enrollments.isEmpty()) {
                    List<LessonProgress> newProgresses = enrollments.stream()
                            .map(enrollment -> LessonProgress.builder()
                                    .enrollment(enrollment)
                                    .lesson(lesson)
                                    .isCompleted(false)
                                    .completedAt(null)
                                    .lastAccessedAt(LocalDateTime.now())
                                    .build()
                            ).toList();
                    lessonProgressRepository.saveAll(newProgresses);
                }
            }
            lesson.setIsPublished(publish);
            lessonRepository.save(lesson);
        }
        List<Enrollment> enrollments = course.getEnrollments();
        if (enrollments != null && !enrollments.isEmpty()) {
            for (Enrollment enrollment : enrollments) {
                Long countLessons = lessonProgressRepository.countALLLessonProgressesByEnrollmentId(enrollment.getId());
                Long countLessonsCompleted = lessonProgressRepository.countAllLessonProgressesCompletedByEnrollmentId(enrollment.getId());
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
                else {
                    enrollment.setCompletionDate(null);
                    enrollment.setStatus(EnrollmentStatus.ENROLLED);
                }
                enrollmentRepository.save(enrollment);
            }
        }
        Lesson savedLesson = lessonRepository.save(lesson);
        return LessonResponse.builder()
                .lessonId(lessonId)
                .courseId(course.getId())
                .title(savedLesson.getTitle())
                .contentUrl(savedLesson.getContentURL())
                .textContent(savedLesson.getTextContent())
                .orderIndex(savedLesson.getOrderIndex())
                .isPublished(savedLesson.getIsPublished())
                .createdAt(savedLesson.getCreatedAt())
                .updatedAt(savedLesson.getUpdatedAt())
                .build();
    }

    @Override
    public void deleteLesson(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId));
        if (lesson.getLessonProgress() != null && !lesson.getLessonProgress().isEmpty()) {
            throw new ConflictException("Không thể xóa bài học khi đang có học sinh theo học!");
        }
        lessonRepository.delete(lesson);
    }

    @Override
    public ContentPreviewResponse getContentPreview(Integer lessonId) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " +  lessonId));
        Course course = courseRepository.findByIdAndIsDeletedFalse(lesson.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        User user = customUserDetails.getUser();
        if (user.getRole() == RoleStatus.STUDENT) {
            if (course.getStatus() == CourseStatus.PUBLISHED) {
                if (!lesson.getIsPublished()) {
                    throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
                }
            }
            else if (course.getStatus() == CourseStatus.ARCHIVED) {
                if (!enrollmentRepository.existsByStudentIdAndCourseId(user.getId(), course.getId())) {
                    throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
                }
                else {
                    if (!lesson.getIsPublished()) {
                        throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
                    }
                }
            }
            else {
                throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
            }
        }
        else if (user.getRole() == RoleStatus.TEACHER) {
            if (course.getStatus() == CourseStatus.DRAFT) {
                throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
            }
            else if (course.getStatus() == CourseStatus.ARCHIVED) {
                if (!course.getTeacher().getId().equals(user.getId())) {
                    throw new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId);
                }
            }
        }
        return ContentPreviewResponse.builder()
                .courseId(course.getId())
                .courseName(course.getTitle())
                .lessonName(lesson.getTitle())
                .textContent(lesson.getTextContent())
                .build();
    }
}