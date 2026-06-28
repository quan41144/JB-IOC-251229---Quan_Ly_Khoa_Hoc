package ra.quan_ly_khoa_hoc.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ra.quan_ly_khoa_hoc.exception.ConflictException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateLessonRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateLessonPublishRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateLessonRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.LessonResponse;
import ra.quan_ly_khoa_hoc.model.entity.Course;
import ra.quan_ly_khoa_hoc.model.entity.CourseStatus;
import ra.quan_ly_khoa_hoc.model.entity.Lesson;
import ra.quan_ly_khoa_hoc.repository.CourseRepository;
import ra.quan_ly_khoa_hoc.repository.EnrollmentRepository;
import ra.quan_ly_khoa_hoc.repository.LessonRepository;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.LessonService;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {
    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
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
    public LessonResponse updateLessonPublish(Integer lessonId, UpdateLessonPublishRequest updateLessonPublishRequest) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại bài học có id " + lessonId));
        Course course = courseRepository.findByIdAndIsDeletedFalse(lesson.getCourse().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + lessonId));
        lesson.setIsPublished(updateLessonPublishRequest.getPublish());
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
}