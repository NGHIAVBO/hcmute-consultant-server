package HcmuteConsultantServer.service.interfaces.admin;

import HcmuteConsultantServer.model.payload.dto.actor.PostDTO;
import HcmuteConsultantServer.model.payload.response.DataResponse;

public interface IAdminPostService {
    DataResponse<PostDTO> approvePost(Integer postId);
}
