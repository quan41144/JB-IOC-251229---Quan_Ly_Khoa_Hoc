package ra.quan_ly_khoa_hoc.service;

import org.apache.coyote.BadRequestException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateLessonRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateLessonPublishRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateLessonRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.LessonResponse;

import java.util.List;

public interface LessonService {
    List<LessonResponse> getAllLessons(Integer courseId);
    LessonResponse getLessonById(Integer lessonId);
    LessonResponse createLesson(Integer courseId, CreateLessonRequest createLessonRequest);
    LessonResponse updateLesson(Integer lessonId, UpdateLessonRequest updateLessonRequest);
    LessonResponse updateLessonPublish(Integer lessonId, UpdateLessonPublishRequest updateLessonPublishRequest);
    void deleteLesson(Integer lessonId);
}
