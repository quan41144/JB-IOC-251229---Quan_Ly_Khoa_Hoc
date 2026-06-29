package ra.quan_ly_khoa_hoc.service;

import ra.quan_ly_khoa_hoc.model.dto.request.CreateNotificationRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.NotificationResponse;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getAllNotifications();
    NotificationResponse updateNotificationIsRead(Integer notificationId);
    NotificationResponse createNotification(CreateNotificationRequest createNotificationRequest);
    void deleteNotificationById(Integer notificationId);
}
