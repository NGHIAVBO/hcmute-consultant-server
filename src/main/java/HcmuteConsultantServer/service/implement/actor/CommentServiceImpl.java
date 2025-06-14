package HcmuteConsultantServer.service.implement.actor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import HcmuteConsultantServer.model.entity.CommentEntity;
import HcmuteConsultantServer.model.entity.PostEntity;
import HcmuteConsultantServer.model.entity.UserInformationEntity;
import HcmuteConsultantServer.model.exception.Exceptions.ErrorException;
import HcmuteConsultantServer.model.payload.dto.actor.CommentDTO;
import HcmuteConsultantServer.model.payload.mapper.actor.CommentMapper;
import HcmuteConsultantServer.model.payload.response.DataResponse;
import HcmuteConsultantServer.repository.actor.CommentRepository;
import HcmuteConsultantServer.repository.admin.UserRepository;
import HcmuteConsultantServer.service.interfaces.actor.ICommentService;
import HcmuteConsultantServer.specification.actor.CommentSpecification;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Override
    public DataResponse<List<CommentDTO>> getAllComments(Integer postId) {
        try {
            List<CommentDTO> result = new ArrayList<>();
            List<CommentEntity> comments = commentRepository.getRootCommentByPostId(postId);

            for (CommentEntity comment : comments) {
                CommentDTO commentDTO = commentMapper.mapToDTO(comment);
                result.add(commentDTO);
            }

            return DataResponse.<List<CommentDTO>>builder()
                    .status("success")
                    .message("Danh sách bình luận")
                    .data(result)
                    .build();
        } catch (Exception e) {
            throw new ErrorException("Đã xảy ra lỗi khi lấy danh sách bình luận.");
        }
    }

    @Override
    public CommentDTO createComment(Integer idPost, String text, String email) {
        Optional<UserInformationEntity> userOpt = userRepository.findUserInfoByEmail(email);
        if (!userOpt.isPresent()) {
            throw new ErrorException("Không tìm thấy người dùng");
        }

        UserInformationEntity user = userOpt.get();
        CommentEntity comment = new CommentEntity();
        comment.setPost(new PostEntity(idPost));
        comment.setComment(text);
        comment.setUserComment(user);
        comment.setCreateDate(LocalDate.now());

        commentRepository.save(comment);

        return commentMapper.mapToDTO(comment);
    }

    @Override
    public CommentDTO replyComment(Integer commentFatherId, String text, String email) {
        Optional<UserInformationEntity> userOpt = userRepository.findUserInfoByEmail(email);
        if (!userOpt.isPresent()) {
            throw new ErrorException("Không tìm thấy người dùng");
        }

        UserInformationEntity user = userOpt.get();
        CommentEntity parentComment = commentRepository.findById(commentFatherId)
                .orElseThrow(() -> new ErrorException("Không tìm thấy bình luận cha."));

        CommentEntity comment = new CommentEntity();
        comment.setParentComment(parentComment);
        comment.setPost(parentComment.getPost());
        comment.setComment(text);
        comment.setUserComment(user);
        comment.setCreateDate(LocalDate.now());

        commentRepository.save(comment);

        return commentMapper.mapToDTO(comment);
    }

    @Override
    public CommentDTO updateComment(Integer idComment, String text, String email) {
        Optional<CommentEntity> commentOpt = commentRepository.findById(idComment);
        if (commentOpt.isEmpty()) {
            throw new ErrorException("Không tìm thấy bình luận.");
        }

        CommentEntity comment = commentOpt.get();

        if (!comment.getUserComment().getAccount().getEmail().equals(email)) {
            throw new ErrorException("Bạn không có quyền cập nhật bình luận này.");
        }

        comment.setComment(text);
        comment.setCreateDate(LocalDate.now());
        commentRepository.save(comment);

        return commentMapper.mapToDTO(comment);
    }

    @Override
    public CommentDTO adminUpdateComment(Integer idComment, String text) {
        Optional<CommentEntity> commentOpt = commentRepository.findById(idComment);
        if (commentOpt.isEmpty()) {
            throw new ErrorException("Không tìm thấy bình luận.");
        }

        CommentEntity comment = commentOpt.get();
        comment.setComment(text);
        comment.setCreateDate(LocalDate.now());
        commentRepository.save(comment);

        return commentMapper.mapToDTO(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Integer idComment, String email) {
        CommentEntity comment = commentRepository.findById(idComment)
                .orElseThrow(() -> new ErrorException("Không tìm thấy bình luận."));

        if (!comment.getUserComment().getAccount().getEmail().equals(email)) {
            throw new ErrorException("Bạn không có quyền xóa bình luận này.");
        }

        List<CommentEntity> children = commentRepository.getCommentByParentComment(idComment);

        for (CommentEntity child : children) {
            String childEmail = child.getUserComment().getAccount().getEmail();

            if (childEmail.equals(email)) {
                deleteComment(child.getIdComment(), email);
            } else {
                child.setParentComment(null);
                commentRepository.save(child);
            }
        }

        commentRepository.deleteById(idComment);
    }


    @Override
    public void adminDeleteComment(Integer idComment) {
        commentRepository.findById(idComment)
                .orElseThrow(() -> new ErrorException("Không tìm thấy bình luận."));
        List<CommentEntity> children = commentRepository.getCommentByParentComment(idComment);

        children.forEach(cmt -> adminDeleteComment(cmt.getIdComment()));
        commentRepository.deleteById(idComment);
    }

    @Override
    public Page<CommentDTO> getCommentsByPostWithPagingAndFilters(Optional<Integer> postId, Optional<LocalDate> startDate, Optional<LocalDate> endDate, Pageable pageable) {
        Specification<CommentEntity> spec = Specification.where(CommentSpecification.hasPostId(postId.orElse(null)));

        if (startDate.isPresent() && endDate.isPresent()) {
            spec = spec.and(CommentSpecification.hasExactDateRange(startDate.get(), endDate.get()));
        } else if (startDate.isPresent()) {
            spec = spec.and(CommentSpecification.hasExactStartDate(startDate.get()));
        } else if (endDate.isPresent()) {
            spec = spec.and(CommentSpecification.hasDateBefore(endDate.get()));
        }

        return commentRepository.findAll(spec, pageable)
                .map(commentMapper::mapToDTO);
    }
}
