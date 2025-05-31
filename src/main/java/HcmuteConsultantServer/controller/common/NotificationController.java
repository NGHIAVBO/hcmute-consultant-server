package HcmuteConsultantServer.controller.common;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import HcmuteConsultantServer.model.entity.NotificationEntity;
import HcmuteConsultantServer.model.entity.UserInformationEntity;
import HcmuteConsultantServer.model.exception.Exceptions.ErrorException;
import HcmuteConsultantServer.model.payload.response.DataResponse;
import HcmuteConsultantServer.repository.admin.UserRepository;
import HcmuteConsultantServer.service.interfaces.common.INotificationService;
import net.bytebuddy.asm.Advice;

@RestController
@RequestMapping("${base.url}")
public class NotificationController {

    @Autowired
    private INotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/notification")
    public ResponseEntity<DataResponse<Page<NotificationEntity>>> getUserNotifications(
            Principal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        String email = principal.getName();
        
        Optional<UserInformationEntity> userOpt = userRepository.findUserInfoByEmail(email);
        if (!userOpt.isPresent()) {
            throw new ErrorException("Không tìm thấy người dùng");
        }

        UserInformationEntity user = userOpt.get();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(sortDir), sortBy));
        
        Page<NotificationEntity> notifications = notificationService.getNotificationsByReceiverId(user.getId(), pageable);

        DataResponse<Page<NotificationEntity>> response = DataResponse.<Page<NotificationEntity>>builder()
                .status("success")
                .message("Danh sách thông báo của người dùng")
                .data(notifications)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/notification/read")
    public ResponseEntity<DataResponse<Void>> readNotification(@RequestParam Integer id) {
        notificationService.readNotification(id);
        return ResponseEntity.ok(DataResponse.<Void>builder()
                .status("success")
                .message("Đã đọc thông báo")    
                .build());
    }
    
    @PostMapping("/notification/read-all")
    public ResponseEntity<DataResponse<Void>> readAllNotifications(Principal principal) {
        String email = principal.getName();
        Optional<UserInformationEntity> userOpt = userRepository.findUserInfoByEmail(email);
        if (!userOpt.isPresent()) {
            throw new ErrorException("Không tìm thấy người dùng");
        }

        UserInformationEntity user = userOpt.get();
        notificationService.readAllNotifications(user.getId());
        return ResponseEntity.ok(DataResponse.<Void>builder()
                .status("success")
                .message("Đã đọc tất cả thông báo")
                .build());
    }

    @GetMapping("notification/detail")
    public ResponseEntity<DataResponse<NotificationEntity>> findNotificationById(@RequestParam Integer id) {
        NotificationEntity notification = notificationService.findNotificationById(id);
        return ResponseEntity.ok(DataResponse.<NotificationEntity>builder()
                .status("success")
                .message("Thông báo")
                .data(notification)
                .build());
    }

    @PostMapping("/notification/delete-all")
    @Transactional
    public ResponseEntity<DataResponse<Void>> deleteAllNotifications(Principal principal) {
        String email = principal.getName();
        Optional<UserInformationEntity> userOpt = userRepository.findUserInfoByEmail(email);
        if (!userOpt.isPresent()) {
            throw new ErrorException("Không tìm thấy người dùng");
        }

        UserInformationEntity user = userOpt.get();
        notificationService.deleteAllNotifications(user.getId());
        return ResponseEntity.ok(DataResponse.<Void>builder()
                .status("success")
                .message("Đã xóa tất cả thông báo")
                .build());
    }
}
