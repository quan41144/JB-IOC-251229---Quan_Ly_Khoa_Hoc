package ra.quan_ly_khoa_hoc.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateNotificationRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.ApiResponse;
import ra.quan_ly_khoa_hoc.service.NotificationService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getMyNotifications() {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Lấy danh sách thông báo thành công!",
                notificationService.getAllNotifications(),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PutMapping("/{notification_id}/read")
    public ResponseEntity<ApiResponse<?>> readNotificationById(@Valid @PathVariable("notification_id") Integer notificationId) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Đã đánh dấu là đã đọc!",
                notificationService.updateNotificationIsRead(notificationId),
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createNotification(@Valid @RequestBody CreateNotificationRequest createNotificationRequest) {
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Tạo thông báo thành công!",
                notificationService.createNotification(createNotificationRequest),
                null,
                LocalDateTime.now()
        ), HttpStatus.CREATED);
    }
    @DeleteMapping("/{notification_id}")
    public ResponseEntity<ApiResponse<?>> deleteNotificationById(@Valid @PathVariable("notification_id") Integer notificationId) {
        notificationService.deleteNotificationById(notificationId);
        return new ResponseEntity<>(new ApiResponse<>(
                true,
                "Xóa thông báo thành công!",
                null,
                null,
                LocalDateTime.now()
        ), HttpStatus.OK);
    }
}
