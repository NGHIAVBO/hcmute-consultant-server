package HcmuteConsultantServer.repository.common;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import HcmuteConsultantServer.model.entity.NotificationEntity;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface NotificationRepository extends PagingAndSortingRepository<NotificationEntity, Integer>, JpaSpecificationExecutor<NotificationEntity> {

    List<NotificationEntity> findByReceiverId(Integer receiverId);

    Page<NotificationEntity> findByReceiverId(Integer receiverId, Pageable pageable);

    void deleteAllByReceiverId(Integer userId);
}
