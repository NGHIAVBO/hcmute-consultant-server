package HcmuteConsultantServer.service.implement.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import HcmuteConsultantServer.model.entity.PostEntity;
import HcmuteConsultantServer.model.exception.Exceptions.ErrorException;
import HcmuteConsultantServer.model.payload.dto.actor.PostDTO;
import HcmuteConsultantServer.model.payload.mapper.admin.PostMapper;
import HcmuteConsultantServer.model.payload.response.DataResponse;
import HcmuteConsultantServer.repository.actor.PostRepository;
import HcmuteConsultantServer.service.interfaces.admin.IAdminPostService;

@Service
public class AdminPostServiceImpl implements IAdminPostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostMapper postMapper;

    @Override
    public DataResponse<PostDTO> approvePost(Integer postId) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new ErrorException("Không tìm thấy bài viết."));
        post.setApproved(true);
        postRepository.save(post);

        PostDTO approvedPostDTO = postMapper.mapToDTO(post);
        return DataResponse.<PostDTO>builder()
                .status("success")
                .message("Bài viết đã được phê duyệt")
                .data(approvedPostDTO)
                .build();
    }
}
