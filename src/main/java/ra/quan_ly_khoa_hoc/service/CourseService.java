package ra.quan_ly_khoa_hoc.service;

import org.apache.coyote.BadRequestException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateCourseStatusRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.CourseResponse;
import ra.quan_ly_khoa_hoc.model.entity.CourseStatus;
import ra.quan_ly_khoa_hoc.model.entity.RoleStatus;
import ra.quan_ly_khoa_hoc.model.entity.User;

import java.util.List;

public interface CourseService {
    List<CourseResponse> getAllCourses(RoleStatus role);
    CourseResponse getCourseById(Integer id);
    CourseResponse createCourse(CreateCourseRequest createCourseRequest) throws BadRequestException;
    CourseResponse updateCourse(Integer id, UpdateCourseRequest updateCourseRequest);
    CourseResponse updateCourseStatus(Integer id, UpdateCourseStatusRequest updateCourseStatusRequest) throws BadRequestException;
    void deleteCourse(Integer id) throws BadRequestException;
}
