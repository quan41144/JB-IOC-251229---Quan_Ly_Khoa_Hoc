package ra.quan_ly_khoa_hoc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateLessonPublishRequest;
import ra.quan_ly_khoa_hoc.model.dto.request.UpdateLessonRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;
import ra.quan_ly_khoa_hoc.service.LessonService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {
    private final LessonService lessonService;
    @GetMapping("/{lesson_id}")
    public ResponseEntity<ApiResponse<?>> getLessonById(@Valid @PathVariable("lesson_id") Integer lessonId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy thông tin chi tiết của bài học có id " + lessonId + " thành công!",
                lessonService.getLessonById(lessonId),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PutMapping("/{lesson_id}")
    public ResponseEntity<ApiResponse<?>> updateLesson(@Valid @PathVariable("lesson_id") Integer lessonId, @Valid @ModelAttribute UpdateLessonRequest updateLessonRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật thông tin bài học thành công!",
                lessonService.updateLesson(lessonId, updateLessonRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PutMapping("/{lesson_id}/publish")
    public ResponseEntity<ApiResponse<?>> updateLessonPublish(@Valid @PathVariable("lesson_id") Integer lessonId, @Valid @RequestBody UpdateLessonPublishRequest updateLessonPublishRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Cập nhật trạng thái hiện thị bài học thành công!",
                lessonService.updateLessonPublish(lessonId, updateLessonPublishRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @DeleteMapping("/{lesson_id}")
    public ResponseEntity<ApiResponse<?>> deleteLesson(@PathVariable("lesson_id") Integer lessonId) {
        lessonService.deleteLesson(lessonId);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa bài học thành công!",
                null,
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @GetMapping("/{lesson_id}/content_preview")
    public ResponseEntity<ApiResponse<?>> getContentPreview(@PathVariable("lesson_id") Integer lessonId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy nội dung xem trước của bài học thành công!",
                lessonService.getContentPreview(lessonId),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
