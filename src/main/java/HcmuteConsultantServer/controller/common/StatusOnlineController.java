package HcmuteConsultantServer.controller.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import HcmuteConsultantServer.constant.SecurityConstants;
import HcmuteConsultantServer.model.payload.dto.actor.UserOnlineDTO;
import HcmuteConsultantServer.repository.admin.AccountRepository;
import HcmuteConsultantServer.repository.admin.UserRepository;
import HcmuteConsultantServer.security.jwt.JwtProvider;
import HcmuteConsultantServer.service.interfaces.common.IStatusOnlineService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class StatusOnlineController {
    @Autowired
    private IStatusOnlineService commonStatusOnlineService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private UserRepository userRepository;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String token = headerAccessor.getFirstNativeHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = jwtProvider.getEmailFromToken(token);

            commonStatusOnlineService.updateStatus(email, true);
            accountRepository.findByEmail(email).ifPresentOrElse(account -> {
                sendOnlineUsersUpdate();
            }, () -> System.out.println("Người dùng không được tìm thấy với email: " + email));
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String token = headerAccessor.getFirstNativeHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String email = jwtProvider.getEmailFromToken(token);

            commonStatusOnlineService.updateStatus(email, false);
            accountRepository.findByEmail(email).ifPresentOrElse(account -> {
                sendOnlineUsersUpdate();
            }, () -> System.out.println("Người dùng không được tìm thấy với email: " + email));
        }
    }

    @Scheduled(fixedRate = 300000) // 5 phút
    public void checkAndUpdateOnlineStatus() {
        LocalDateTime now = LocalDateTime.now();

        commonStatusOnlineService.getOnlineUsers().forEach((email, lastActiveTime) -> {
            long secondsInactive = Duration.between(lastActiveTime, now).toSeconds();
            if (secondsInactive >= 300) {
                commonStatusOnlineService.updateStatus(email, false);

                accountRepository.findByEmail(email).ifPresent(account -> {
                    System.out.println("Cập nhật trạng thái offline cho: " + email);
                });
            }
        });

        sendOnlineUsersUpdate();
    }

    private void sendOnlineUsersUpdate() {
        List<UserOnlineDTO> onlineUsers = getOnlineUsers();
        List<UserOnlineDTO> allUsers = getAllUsers();

        for (UserOnlineDTO receiver : allUsers) {
            String destination = "/user/" + receiver.getId() + "/online-users";
            messagingTemplate.convertAndSend(destination, onlineUsers);
        }
    }

    public List<UserOnlineDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(user -> new UserOnlineDTO(
                        user.getId(),
                        user.getName(),
                        user.getAccount().getEmail(),
                        user.getPhone(),
                        "Online",
                        user.getAvatarUrl()
                ))
                .collect(Collectors.toList());
    }

    private List<UserOnlineDTO> getOnlineUsers() {
        LocalDateTime now = LocalDateTime.now();

        return commonStatusOnlineService.getOnlineUsers().entrySet().stream()
                .filter(entry -> {
                    long secondsInactive = Duration.between(entry.getValue(), now).toSeconds();
                    return secondsInactive < 300;
                })
                .map(entry -> {
                    String email = entry.getKey();
                    return accountRepository.findByEmail(email).map(account -> {
                        String role = SecurityConstants.Role.TUVANVIEN;
                        if (account.getRole() != null && role.equals(account.getRole().getName())) {
                            return new UserOnlineDTO(
                                    account.getId(),
                                    account.getName(),
                                    account.getEmail(),
                                    account.getPhone(),
                                    "Online",
                                    account.getUserInformation().getAvatarUrl()
                            );
                        }
                        return null;
                    }).orElse(null);
                })
                .filter(userOnlineDTO -> userOnlineDTO != null)
                .collect(Collectors.toList());
    }
}
