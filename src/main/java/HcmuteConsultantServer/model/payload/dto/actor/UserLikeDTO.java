package HcmuteConsultantServer.model.payload.dto.actor;

import lombok.Data;

@Data
public class UserLikeDTO {
    private Integer id;
    private String firstName;
    private String lastName;
    private String avatarUrl;
} 