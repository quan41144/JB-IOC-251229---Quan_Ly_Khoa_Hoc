package ra.quan_ly_khoa_hoc.service.Impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ra.quan_ly_khoa_hoc.exception.AccessDeniedException;
import ra.quan_ly_khoa_hoc.exception.BadRequestException;
import ra.quan_ly_khoa_hoc.exception.ResourceNotFoundException;
import ra.quan_ly_khoa_hoc.model.dto.request.CreateNotificationRequest;
import ra.quan_ly_khoa_hoc.model.dto.response.NotificationResponse;
import ra.quan_ly_khoa_hoc.model.entity.Notification;
import ra.quan_ly_khoa_hoc.model.entity.User;
import ra.quan_ly_khoa_hoc.repository.NotificationRepository;
import ra.quan_ly_khoa_hoc.repository.UserRepository;
import ra.quan_ly_khoa_hoc.security.user_detail.CustomUserDetails;
import ra.quan_ly_khoa_hoc.service.NotificationService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Override
    public List<NotificationResponse> getAllNotifications() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = customUserDetails.getUser().getId();
        List<Notification> notifications = notificationRepository.findAllNotificationsByUserId(currentUserId);
        return notifications.stream()
                .map(n -> NotificationResponse.builder()
                        .notificationId(n.getId())
                        .message(n.getMessage())
                        .type(n.getType())
                        .targetUrl(n.getTargetUrl())
                        .isRead(n.getIsRead())
                        .build()
                ).toList();
    }

    @Override
    @Transactional
    public NotificationResponse updateNotificationIsRead(Integer notificationId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails customUserDetails = (CustomUserDetails) auth.getPrincipal();
        Integer currentUserId = customUserDetails.getUser().getId();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại thông báo có id " + notificationId));
        if (notification.getUser() != null && !notification.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Không được sửa!");
        }
        notification.setIsRead(true);
        Notification savedNotification = notificationRepository.save(notification);
        return NotificationResponse.builder()
                .notificationId(savedNotification.getId())
                .message(savedNotification.getMessage())
                .type(savedNotification.getType())
                .targetUrl(savedNotification.getTargetUrl())
                .isRead(savedNotification.getIsRead())
                .build();
    }

    @Override
    public NotificationResponse createNotification(CreateNotificationRequest createNotificationRequest) {
        User user = userRepository.findByIdAndIsDeletedFalse(createNotificationRequest.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại người dùng có id " + createNotificationRequest.getUserId()));
        Notification notification = Notification.builder()
                .user(user)
                .message(createNotificationRequest.getMessage())
                .type(createNotificationRequest.getType())
                .targetUrl(createNotificationRequest.getTargetUrl())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
        Notification savedNotification = notificationRepository.save(notification);
        return NotificationResponse.builder()
                .notificationId(savedNotification.getId())
                .message(savedNotification.getMessage())
                .type(savedNotification.getType())
                .targetUrl(savedNotification.getTargetUrl())
                .isRead(savedNotification.getIsRead())
                .build();
    }

    @Override
    public void deleteNotificationById(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tồn tại thông báo có id " +  notificationId));
        notificationRepository.delete(notification);
    }
}
