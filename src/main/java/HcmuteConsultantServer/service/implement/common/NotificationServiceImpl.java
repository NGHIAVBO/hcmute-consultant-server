package HcmuteConsultantServer.service.implement.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import HcmuteConsultantServer.constant.enums.NotificationStatus;
import HcmuteConsultantServer.constant.enums.NotificationType;
import HcmuteConsultantServer.model.entity.ConsultationScheduleEntity;
import HcmuteConsultantServer.model.entity.ConsultationScheduleRegistrationEntity;
import HcmuteConsultantServer.model.entity.NotificationEntity;
import HcmuteConsultantServer.model.exception.Exceptions;
import HcmuteConsultantServer.model.payload.dto.common.NotificationResponseDTO;
import HcmuteConsultantServer.model.payload.dto.common.NotificationResponseDTO.NotificationDTO;
import HcmuteConsultantServer.repository.actor.ConsultationScheduleRegistrationRepository;
import HcmuteConsultantServer.repository.actor.ConsultationScheduleRepository;
import HcmuteConsultantServer.repository.common.NotificationRepository;
import HcmuteConsultantServer.security.config.Email.EmailService;
import HcmuteConsultantServer.service.interfaces.common.INotificationService;

@Service
public class NotificationServiceImpl implements INotificationService {

    private final NotificationRepository notificationRepository;
    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private ConsultationScheduleRegistrationRepository registrationRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ConsultationScheduleRepository consultationScheduleRepository;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    public NotificationServiceImpl(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void sendUserNotification(Integer senderId, Integer receiverId, String content, NotificationType type) {
        NotificationEntity notification = NotificationEntity.builder()
                .senderId(senderId)
                .receiverId(receiverId)
                .content(content)
                .time(LocalDateTime.now())
                .notificationType(type)
                .status(NotificationStatus.UNREAD)
                .build();

        notificationRepository.save(notification);

        NotificationDTO notificationDTO = NotificationDTO.builder()
                .senderId(notification.getSenderId())
                .receiverId(notification.getReceiverId())
                .content(notification.getContent())
                .time(notification.getTime())
                .notificationType(notification.getNotificationType().name())
                .status(notification.getStatus().name())
                .build();

        NotificationResponseDTO responseDTO = NotificationResponseDTO.builder()
                .status("notification")
                .data(notificationDTO)
                .build();

        simpMessagingTemplate.convertAndSendToUser(String.valueOf(receiverId), "/notification", responseDTO);
    }

    @Scheduled(cron = "0,01 * * * * ?")
    public void sendEmailNotificationsForUpcomingSchedules() throws MessagingException {
        List<ConsultationScheduleEntity> upcomingSchedules = consultationScheduleRepository
                .findByConsultationDateAfterAndStatusConfirmedTrue(LocalDate.now());

        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (ConsultationScheduleEntity schedule : upcomingSchedules) {
            LocalTime consultationTime = LocalTime.parse(schedule.getConsultationTime(), timeFormatter);
            LocalDate consultationDate = schedule.getConsultationDate();

            LocalDateTime consultationDateTime = consultationDate.atTime(consultationTime);

            if (consultationDateTime.isAfter(LocalDateTime.now()) &&
                    consultationDateTime.isBefore(LocalDateTime.now().plusDays(1))) {
                sendReminderEmail(consultationDateTime, schedule,  24*60);
            }
            if (consultationDateTime.isAfter(LocalDateTime.now()) &&
                    consultationDateTime.isBefore(LocalDateTime.now().plusDays(1))) {
                sendReminderEmailToUser(consultationDateTime, schedule,  24*60);
            }
        }
    }

    private void sendReminderEmail(LocalDateTime consultationDateTime, ConsultationScheduleEntity schedule, int minutesBefore) throws MessagingException {
        LocalDateTime reminderTime = consultationDateTime.minusMinutes(minutesBefore);
        LocalDateTime currentTime = LocalDateTime.now();

        if (currentTime.isAfter(reminderTime.minusMinutes(1)) && currentTime.isBefore(reminderTime.plusMinutes(1))) {
            List<ConsultationScheduleRegistrationEntity> registrations = registrationRepository
                    .findByConsultationSchedule(schedule);

            for (ConsultationScheduleRegistrationEntity registration : registrations) {
                String userEmail = registration.getUser().getAccount().getEmail();
                String userFullName = registration.getUser().getLastName() + " " + registration.getUser().getFirstName();

                MimeMessage mailMessage = javaMailSender.createMimeMessage();
                MimeMessageHelper mailHelper = new MimeMessageHelper(mailMessage, true, "UTF-8");

                String emailContent = "<html><head><meta charset=\"UTF-8\"></head><body>"
                        + "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333;\">"
                        + "    <h2 style=\"color: #0066cc; text-align: center;\">Lịch tư vấn sắp diễn ra</h2>"
                        + "    <p>Chào <strong>" + userFullName + "</strong>,</p>"
                        + "    <p>Lịch tư vấn: <strong>" + schedule.getTitle() + "</strong> sẽ diễn ra trong <strong>1 ngày nữa</strong>.</p>"
                        + "    <p>Chúng tôi rất mong được gặp bạn.</p>"
                        + "    <br>"
                        + "    <p style=\"font-size: 14px; text-align: center; color: #888;\">Trân trọng!</p>"
                        + "    <hr style=\"border: 0; border-top: 1px solid #ccc;\">"
                        + "    <p style=\"font-size: 12px; text-align: center; color: #888;\">Đây là email tự động, vui lòng không trả lời.</p>"
                        + "</div>"
                        + "</body></html>";

                mailHelper.setFrom("ngoquangnghia111003@gmail.com");
                mailHelper.setTo(userEmail);
                mailHelper.setSubject("Lịch tư vấn sắp diễn ra");
                mailHelper.setText(emailContent, true);

                emailService.sendEmail(mailMessage);
            }
        }
    }

    private void sendReminderEmailToUser(LocalDateTime consultationDateTime, ConsultationScheduleEntity schedule, int minutesBefore) throws MessagingException {
        LocalDateTime reminderTime = consultationDateTime.minusMinutes(minutesBefore);
        LocalDateTime currentTime = LocalDateTime.now();

        if (currentTime.isAfter(reminderTime.minusMinutes(1)) && currentTime.isBefore(reminderTime.plusMinutes(1))) {
            String userEmail = schedule.getUser().getAccount().getEmail();
            String userFullName = schedule.getUser().getLastName() + " " + schedule.getUser().getFirstName();

            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mailHelper = new MimeMessageHelper(mailMessage, true, "UTF-8");

            String emailContent = "<html><head><meta charset=\"UTF-8\"></head><body>"
                    + "<div style=\"font-family: Arial, sans-serif; padding: 20px; color: #333;\">"
                    + "    <h2 style=\"color: #0066cc; text-align: center;\">Lịch tư vấn sắp diễn ra</h2>"
                    + "    <p>Chào <strong>" + userFullName + "</strong>,</p>"
                    + "    <p>Lịch tư vấn: <strong>" + schedule.getTitle() + "</strong> sẽ diễn ra trong <strong>1 ngày nữa</strong>.</p>"
                    + "    <p>Hãy chuẩn bị sẵn sàng cho buổi tư vấn.</p>"
                    + "    <br>"
                    + "    <p style=\"font-size: 14px; text-align: center; color: #888;\">Trân trọng!</p>"
                    + "    <hr style=\"border: 0; border-top: 1px solid #ccc;\">"
                    + "    <p style=\"font-size: 12px; text-align: center; color: #888;\">Đây là email tự động, vui lòng không trả lời.</p>"
                    + "</div>"
                    + "</body></html>";

            mailHelper.setFrom("ngoquangnghia111003@gmail.com");
            mailHelper.setTo(userEmail);
            mailHelper.setSubject("Lịch tư vấn sắp diễn ra");
            mailHelper.setText(emailContent, true);

            emailService.sendEmail(mailMessage);
        }
    }

    @Override
    public void readNotification(Integer notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exceptions.ErrorException("Thông báo không tồn tại"));
        notification.setStatus(NotificationStatus.READ);
        notificationRepository.save(notification);
    }

    @Override
    public void readAllNotifications(Integer userId) {
        List<NotificationEntity> notifications = notificationRepository.findByReceiverId(userId);
        for (NotificationEntity notification : notifications) {
            notification.setStatus(NotificationStatus.READ);
        }
        notificationRepository.saveAll(notifications);
    }

    @Override
    public NotificationEntity findNotificationById(Integer notificationId) {
        return notificationRepository.findById(notificationId)
                .orElseThrow(() -> new Exceptions.ErrorException("Thông báo không tồn tại"));
    }

    @Override
    public Page<NotificationEntity> getNotificationsByReceiverId(Integer receiverId, Pageable pageable) {
        return notificationRepository.findByReceiverId(receiverId, pageable);
    }

    @Override
    public void deleteAllNotifications(Integer userId) {
        notificationRepository.deleteAllByReceiverId(userId);
    }
}
