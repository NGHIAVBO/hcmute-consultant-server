package HcmuteConsultantServer.service.interfaces.actor;

import java.util.List;

import HcmuteConsultantServer.model.entity.LikeRecordEntity;
import HcmuteConsultantServer.model.entity.UserInformationEntity;
import HcmuteConsultantServer.model.payload.dto.actor.UserLikeDTO;

public interface ILikeService {

    List<LikeRecordEntity> getLikeRecordByPostId(Integer postId);

    List<LikeRecordEntity> getLikeRecordByCommentId(Integer commentId);

    List<LikeRecordEntity> getLikeRecordByQuestionId(Integer questionId);

    void likePost(Integer postId, Integer userId);

    void unlikePost(Integer postId, Integer userId);

    void likeComment(Integer commentId, Integer userId);

    void unlikeComment(Integer commentId, Integer userId);

    Integer getUserIdByEmail(String email);

    Integer countLikesByPostId(Integer postId);

    Integer countLikesByCommentId(Integer commentId);



    void likeQuestion(Integer questionId, Integer userId);

    void unlikeQuestion(Integer questionId, Integer userId);

    Integer countLikesByQuestionId(Integer questionId);

    boolean existsByUserAndPost(UserInformationEntity user, Integer postId);
    boolean existsByUserAndComment(UserInformationEntity user, Integer commentId);
    boolean existsByUserAndQuestion(UserInformationEntity user, Integer questionId);

    List<UserLikeDTO> getLikeUsersOfPost(Integer postId);

    List<UserLikeDTO> getLikeUsersOfComment(Integer commentId);

    List<UserLikeDTO> getLikeUsersOfQuestion(Integer questionId);


    }
