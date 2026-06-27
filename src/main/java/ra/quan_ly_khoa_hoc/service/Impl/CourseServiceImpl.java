package ra.quan_ly_khoa_hoc.service.Impl;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseStatusRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.CourseResponse;
import ra.quan_ly_khoa_hoc.model.dto.response.LessonResponse;
import ra.quan_ly_khoa_hoc.model.entity.Course;
import ra.quan_ly_khoa_hoc.model.entity.CourseStatus;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;
import ra.quan_ly_khoa_hoc.model.entity.User;
import ra.quan_ly_khoa_hoc.repository.CourseRepository;
import ra.quan_ly_khoa_hoc.repository.LessonRepository;
import ra.quan_ly_khoa_hoc.repository.UserRepository;
import ra.quan_ly_khoa_hoc.service.CourseService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {
    private final CourseRepository courseRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    @Override
    public List<CourseResponse> getAllCourses(User currentUser) {
        List<Course> courses;
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .contains(new SimpleGrantedAuthority("ROLE_ADMIN"));
        if (isAdmin) {
            courses = courseRepository.findAll();
        }
        else {
            courses = courseRepository.findCoursesByStatus(CourseStatus.PUBLISHED);
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
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Khóa học có id " + id + " không tồn tại!"));
        List<LessonResponse> lessons = lessonRepository
                .findByCourseIdAndIsPublishedTrueOrderByOrderIndex(id)
                .stream()
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
                .lessons(lessons)
                .build();
    }

    @Override
    public CourseResponse createCourse(CreateCourseRequest createCourseRequest) throws BadRequestException {
        User teacher = userRepository.findById(createCourseRequest.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại giáo viên có id " + createCourseRequest.getTeacherId()));
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
    public CourseResponse updateCourse(Integer id, UpdateCourseRequest updateCourseRequest) throws BadRequestException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + id ));
        if (updateCourseRequest.getTitle() != null) {
            course.setTitle(updateCourseRequest.getTitle());
        }
        if (updateCourseRequest.getDescription() != null) {
            course.setDescription(updateCourseRequest.getDescription());
        }
        if (updateCourseRequest.getTeacherId() != null) {
            User user = userRepository.findById(updateCourseRequest.getTeacherId())
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
    public CourseResponse updateCourseStatus(Integer id, UpdateCourseStatusRequest updateCourseStatusRequest) throws BadRequestException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + id));
        if (course.getStatus() == CourseStatus.PUBLISHED && updateCourseStatusRequest.getStatus() == CourseStatus.DRAFT) {
            throw new BadRequestException("Không được thay đổi trạng thái của khóa học đã xuất bản hoặc có học sinh đang theo học trở thành trạng thái bản nháp!");
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
    public void deleteCourse(Integer id) throws BadRequestException {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại khóa học có id " + id));
        if (course.getStatus() == CourseStatus.PUBLISHED) {
            throw new BadRequestException("Không được xóa khóa học đang được xuất bản hoặc có học sinh theo học!");
        }
        if (!course.getLessons().isEmpty() || !course.getEnrollments().isEmpty() || !course.getReviews().isEmpty()) {
            throw new BadRequestException("Không được xóa khóa học khi đang chứa lessons, enrollments hoặc reviews");
        }
        courseRepository.deleteById(id);
    }
}
