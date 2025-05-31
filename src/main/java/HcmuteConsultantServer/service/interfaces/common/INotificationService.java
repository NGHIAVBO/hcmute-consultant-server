package HcmuteConsultantServer.service.interfaces.common;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import HcmuteConsultantServer.constant.enums.NotificationType;
import HcmuteConsultantServer.model.entity.NotificationEntity;

public interface INotificationService {
    void sendUserNotification(Integer senderId, Integer receiverId, String content, NotificationType type);

    void readNotification(Integer notificationId);

    void readAllNotifications(Integer userId);

    NotificationEntity findNotificationById(Integer notificationId);

    Page<NotificationEntity> getNotificationsByReceiverId(Integer receiverId, Pageable pageable);

    @Transactional
    void deleteAllNotifications(Integer userId);
}
