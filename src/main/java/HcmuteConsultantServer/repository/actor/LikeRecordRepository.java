package HcmuteConsultantServer.repository.actor;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import HcmuteConsultantServer.model.entity.LikeKeyEntity;
import HcmuteConsultantServer.model.entity.LikeRecordEntity;
import HcmuteConsultantServer.model.entity.UserInformationEntity;

@Repository
public interface LikeRecordRepository extends JpaRepository<LikeRecordEntity, LikeKeyEntity> {
    @Query("SELECT l FROM LikeRecordEntity l WHERE l.likeKey.targetId = :postId AND l.likeKey.type = 'post'")
    List<LikeRecordEntity> getLikeRecordsByPostId(@Param("postId") Integer postId);

    @Query("SELECT l FROM LikeRecordEntity l WHERE l.likeKey.targetId = :commentId AND l.likeKey.type = 'comment'")
    List<LikeRecordEntity> getLikeRecordsByCommentId(@Param("commentId") Integer commentId);

    @Query("SELECT l FROM LikeRecordEntity l WHERE l.likeKey.targetId = :questionId AND l.likeKey.type = 'question'")
    List<LikeRecordEntity> getLikeRecordsByQuestionId(@Param("questionId") Integer questionId);

    @Query("SELECT u FROM UserInformationEntity u JOIN LikeRecordEntity l ON u.id = l.likeKey.userId WHERE l.likeKey.targetId = :postId AND l.likeKey.type = :type")
    List<UserInformationEntity> getLikeUsersOfPost(@Param("postId") Integer postId, @Param("type") String type);

    @Query("SELECT u FROM UserInformationEntity u JOIN LikeRecordEntity l ON u.id = l.likeKey.userId WHERE l.likeKey.targetId = :commentId AND l.likeKey.type = :type")
    List<UserInformationEntity> getLikeUsersOfComment(@Param("commentId") Integer commentId, @Param("type") String type);

    @Query("SELECT u FROM UserInformationEntity u JOIN LikeRecordEntity l ON u.id = l.likeKey.userId WHERE l.likeKey.targetId = :questionId AND l.likeKey.type = :type")
    List<UserInformationEntity> getLikeUsersOfQuestion(@Param("questionId") Integer questionId, @Param("type") String type);

    Integer countByLikeKeyTargetIdAndLikeKeyType(Integer targetId, String type);

    boolean existsByLikeKeyUserIdAndLikeKeyTargetIdAndLikeKeyType(Integer userId, Integer targetId, String type);
}
