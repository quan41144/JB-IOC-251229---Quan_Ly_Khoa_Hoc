package ra.quan_ly_khoa_hoc.service;

import ra.quan_ly_khoa_hoc.model.dto.request.CreateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseStatusRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.CourseResponse;
import ra.quan_ly_khoa_hoc.model.entity.CourseStatus;

import java.util.List;

public interface CourseService {
    List<CourseResponse> getAllCourses(String keyword, Integer teacherId, CourseStatus status);
    CourseResponse getCourseById(Integer id);
    CourseResponse createCourse(CreateCourseRequest createCourseRequest);
    CourseResponse updateCourse(Integer id, UpdateCourseRequest updateCourseRequest);
    CourseResponse updateCourseStatus(Integer id, UpdateCourseStatusRequest updateCourseStatusRequest);
    void deleteCourse(Integer id);
}
