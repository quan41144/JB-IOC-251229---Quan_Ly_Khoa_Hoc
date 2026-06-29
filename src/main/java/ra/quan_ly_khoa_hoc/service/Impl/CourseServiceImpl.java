package ra.quan_ly_khoa_hoc.service.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ra.quan_ly_khoa_hoc.exception.BadRequestException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseStatusRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.CourseResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.LessonResponse;
import ra.quan_ly_khoa_hoc.model.entity.*;
import ra.quan_ly_khoa_hoc.repository.CourseRepository;
import ra.quan_ly_khoa_hoc.repository.EnrollmentRepository;
import ra.quan_ly_khoa_hoc.repository.LessonRepository;
import ra.quan_ly_khoa_hoc.repository.UserRepository;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.CourseService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public List<CourseResponse> getAllCourses() {
        List<Course> courses;
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        boolean isStudent = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_STUDENT"));
        if (isAdmin) {
            courses = courseRepository.findAllByIsDeletedFalse();
        }
        else if (isStudent) {
            List<Course> published = courseRepository.findCoursesByStatusAndIsDeletedFalse(CourseStatus.PUBLISHED);
            Integer studentId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getUser().getId();
            List<Course> archived = courseRepository.findArchivedCoursesByStudentId(studentId);
            courses = Stream.concat(archived.stream(), published.stream()).toList();
        }
        else {
            Integer teacherId = ((CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                    .getUser().getId();
            List<Course> published = courseRepository.findCoursesByStatusAndIsDeletedFalse(CourseStatus.PUBLISHED);

            List<Course> ownCourses = courseRepository.findByTeacherIdAndIsDeletedFalse(teacherId);
            courses = Stream.concat(published.stream(), ownCourses.stream()).distinct().toList();
        }
        return courses.stream().map(course -> CourseResponse.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(course.getTeacher().getId())
                .teacherName(course.getTeacher().getFullName())
                .price(course.getPrice())
                .duration(course.getDuration())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .lessons(null)
                .build()
        ).toList();
    }

    @Override
    public CourseResponse getCourseById(Integer id) {
        Course course = courseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học có id " + id + " không tồn tại!"));
        boolean isStudent = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_STUDENT"));
        boolean isTeacher = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_TEACHER"));
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (course.getStatus() == CourseStatus.DRAFT) {
            if (isTeacher) {
                CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
                if (!course.getTeacher().getId().equals(customUserDetails.getUser().getId())) {
                    throw new ResourceNotFoundException("Khóa học có id " + id + " không tồn tại!");
                }
            }
            else if (isStudent) {
                throw new ResourceNotFoundException("Khóa học có id " + id + " không tồn tại!");
            }
        }
        if (course.getStatus() == CourseStatus.ARCHIVED && isStudent) {
            CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
            if (!enrollmentRepository.existsByStudentIdAndCourseId(customUserDetails.getUser().getId(), course.getId())) {
                throw new ResourceNotFoundException("Khóa học có id " + id + " không tồn tại!");
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
                        .courseId(id)
                        .title(lesson.getTitle())
                        .contentUrl(lesson.getContentURL())
                        .textContent(lesson.getTextContent())
                        .orderIndex(lesson.getOrderIndex())
                        .isPublished(lesson.getIsPublished())
                        .createdAt(lesson.getCreatedAt())
                        .updatedAt(lesson.getUpdatedAt())
                        .build()
                ).toList();
        return CourseResponse.builder()
                .courseId(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .teacherId(course.getTeacher().getId())
                .teacherName(course.getTeacher().getFullName())
                .price(course.getPrice())
                .duration(course.getDuration())
                .status(course.getStatus())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .lessons(lessonResponses)
                .build();
    }

    @Override
    public CourseResponse createCourse(CreateCourseRequest createCourseRequest) {
        User teacher = userRepository.findByIdAndIsDeletedFalse(createCourseRequest.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại người dùng có id " + createCourseRequest.getTeacherId()));
        if (teacher.getRole() != RoleStatus.TEACHER) {
            throw new BadRequestException("Người dùng có id " + createCourseRequest.getTeacherId() + " không phải giáo viên");
        }
        Course course = Course.builder()
                .title(createCourseRequest.getTitle())
                .description(createCourseRequest.getDescription())
                .teacher(teacher)
                .price(createCourseRequest.getPrice())
                .duration(createCourseRequest.getDuration())
                .build();
        Course savedCourse = courseRepository.save(course);
        return CourseResponse.builder()
                .courseId(savedCourse.getId())
                .title(savedCourse.getTitle())
                .description(savedCourse.getDescription())
                .teacherId(savedCourse.getTeacher().getId())
                .teacherName(savedCourse.getTeacher().getFullName())
                .price(savedCourse.getPrice())
                .duration(savedCourse.getDuration())
                .status(savedCourse.getStatus())
                .createdAt(savedCourse.getCreatedAt())
                .updatedAt(savedCourse.getUpdatedAt())
                .lessons(null)
                .build();
    }

    @Override
    public CourseResponse updateCourse(Integer id, UpdateCourseRequest updateCourseRequest) {
        Course course = courseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + id ));
        if (updateCourseRequest.getTitle() != null) {
            course.setTitle(updateCourseRequest.getTitle());
        }
        if (updateCourseRequest.getDescription() != null) {
            course.setDescription(updateCourseRequest.getDescription());
        }
        if (updateCourseRequest.getTeacherId() != null) {
            User user = userRepository.findByIdAndIsDeletedFalse(updateCourseRequest.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại người dùng có id " + updateCourseRequest.getTeacherId()));
            if (user.getRole() != RoleStatus.TEACHER) {
                throw new BadRequestException("Người dùng có id " + updateCourseRequest.getTeacherId() + " không phải giáo viên!");
            }
            course.setTeacher(user);
        }
        if (updateCourseRequest.getPrice() != null) {
            course.setPrice(updateCourseRequest.getPrice());
        }
        if (updateCourseRequest.getDuration() != null) {
            course.setDuration(updateCourseRequest.getDuration());
        }
        Course savedCourse = courseRepository.save(course);
        return CourseResponse.builder()
                .courseId(savedCourse.getId())
                .title(savedCourse.getTitle())
                .description(savedCourse.getDescription())
                .teacherId(savedCourse.getTeacher().getId())
                .teacherName(savedCourse.getTeacher().getFullName())
                .price(savedCourse.getPrice())
                .duration(savedCourse.getDuration())
                .status(savedCourse.getStatus())
                .createdAt(savedCourse.getCreatedAt())
                .updatedAt(savedCourse.getUpdatedAt())
                .lessons(null)
                .build();
    }

    @Override
    @Transactional
    public CourseResponse updateCourseStatus(Integer id, UpdateCourseStatusRequest updateCourseStatusRequest) {
        Course course = courseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + id));
        if (course.getStatus() == CourseStatus.PUBLISHED && updateCourseStatusRequest.getStatus() == CourseStatus.DRAFT) {
            throw new BadRequestException("Không được thay đổi trạng thái của khóa học đã xuất bản trở thành trạng thái bản nháp!");
        }
        if (!course.getEnrollments().isEmpty() && updateCourseStatusRequest.getStatus() == CourseStatus.DRAFT) {
            throw new BadRequestException("Không được thay đổi trạng thái của khóa học đang có học sinh học trở thành trạng thái bản nháp!");
        }
        course.setStatus(updateCourseStatusRequest.getStatus());
        Course savedCourse = courseRepository.save(course);
        return CourseResponse.builder()
                .courseId(savedCourse.getId())
                .title(savedCourse.getTitle())
                .description(savedCourse.getDescription())
                .teacherId(savedCourse.getTeacher().getId())
                .teacherName(savedCourse.getTeacher().getFullName())
                .price(savedCourse.getPrice())
                .duration(savedCourse.getDuration())
                .status(savedCourse.getStatus())
                .createdAt(savedCourse.getCreatedAt())
                .updatedAt(savedCourse.getUpdatedAt())
                .lessons(null)
                .build();
    }

    @Override
    @Transactional
    public void deleteCourse(Integer id) {
        Course course = courseRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + id));
        if (!course.getEnrollments().isEmpty()) {
            throw new BadRequestException("Không được xóa khóa học đang có học sinh theo học!");
        }
        course.setIsDeleted(true);
        course.setStatus(CourseStatus.ARCHIVED);
        courseRepository.save(course);
    }
}
