package HcmuteConsultantServer.constant.enums;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum NotificationContent {
    NEW_ANSWER("Bạn vừa nhận được câu trả lời cho câu hỏi của mình bởi %s"),
    REVIEW_ANSWER("Bạn vừa nhận được một câu trả lời mới cho câu hỏi của mình bởi %s"),
    REVIEW_ANSWER_CONSULTANT("Bạn vừa nhận được đánh giá cho câu trả lời của mình bởi %s"),
    NEW_CHAT_PRIVATE("Bạn vừa nhận được một tin nhắn riêng bởi %s"),
    NEW_CHAT_GROUP("Bạn vừa nhận được một tin nhắn trong nhóm %s bởi %s"),
    NEW_CONSULTATION_SCHEDULE("Bạn vừa nhận được lịch tư vấn mới bởi %s"),
    CONSULTATION_SCHEDULE_CONFIRMED("Lịch tư vấn của bạn vừa được xác nhận bởi %s"),
    NEW_CONSULTATION_SCHEDULE_ADMIN("Bạn vừa nhận được thông tin buổi tư vấn bởi %s"),
    NEW_CONSULTATION_PARTICIPANT("Bạn vừa nhận được thông báo có người mới tham gia buổi tư vấn bởi %s"),
    NEW_POST_CREATED("Bạn vừa nhận được một bài viết mới bởi %s"),
    APPROVE_POST("Bài viết gần đây của bạn vừa được phê duyệt bởi %s"),
    NEW_QUESTION("Bạn vừa nhận được một câu hỏi mới bởi %s"),
    DELETE_QUESTION("Câu hỏi của bạn vừa bị xóa bởi %s"),
    NEW_COMMON_QUESTION("Bạn vừa nhận được một câu hỏi chung bởi %s"),
    FORWARD_QUESTION_RECEIVED("Bạn vừa nhận được một câu hỏi được chuyển tiếp bởi %s"),
    FORWARD_QUESTION_SENT("Câu hỏi của bạn vừa được chuyển tiếp bởi %s"),
    NEW_COMMENT("Bài viết của bạn vừa nhận được một bình luận mới bởi %s"),
    NEW_REPLY_POST("Bài viết của bạn vừa nhận được một phản hồi mới bởi %s"),
    NEW_REPLY_COMMENT("Bình luận của bạn vừa được phản hồi bởi %s"),
    LIKE_POST("Bài viết của bạn vừa được thích bởi %s"),
    LIKE_COMMENT("Bình luận của bạn vừa được thích bởi %s"),
    LIKE_QUESTION("Câu hỏi của bạn vừa được thích bởi %s"),
    NEW_CONVERSATION_CREATED("Bạn vừa nhận được thông tin một cuộc trò chuyện mới bởi %s"),
    NEW_RATING_RECEIVED("Bạn vừa nhận được một đánh giá mới bởi %s");



    private final String message;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");

    NotificationContent(String message) {
        this.message = message;
    }

    public String formatMessage(String senderName) {
        String formattedMessage = String.format(message, senderName);
        return formattedMessage + " vào lúc " + LocalDateTime.now().format(formatter);
    }

    public String formatMessage(String... params) {
        String formattedMessage = String.format(message, (Object[]) params);
        return formattedMessage + " vào lúc " + LocalDateTime.now().format(formatter);
    }
}

