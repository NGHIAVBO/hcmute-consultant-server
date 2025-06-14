package HcmuteConsultantServer.specification.actor;

import org.springframework.data.jpa.domain.Specification;
import HcmuteConsultantServer.model.entity.AccountEntity;
import HcmuteConsultantServer.model.entity.RoleEntity;
import HcmuteConsultantServer.model.entity.DepartmentEntity;
import HcmuteConsultantServer.model.entity.CommonQuestionEntity;
import HcmuteConsultantServer.model.entity.UserInformationEntity;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import java.time.LocalDate;

public class CommonQuestionSpecification {

    public static Specification<CommonQuestionEntity> hasStatusTrue() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("status"), true);
    }

    public static Specification<CommonQuestionEntity> isCreatedByAdvisor(Integer departmentId) {
        return (root, query, cb) -> {
            Join<CommonQuestionEntity, UserInformationEntity> userJoin = root.join("createdBy");
            Join<UserInformationEntity, AccountEntity> accountJoin = userJoin.join("account");
            Join<AccountEntity, RoleEntity> roleJoin = accountJoin.join("role");
            Join<AccountEntity, DepartmentEntity> departmentJoin = accountJoin.join("department");
            return cb.and(
                    cb.equal(roleJoin.get("name"), "ROLE_TRUONGBANTUVAN"),
                    cb.equal(departmentJoin.get("id"), departmentId)
            );
        };
    }

    public static Specification<CommonQuestionEntity> hasDepartment(Integer departmentId) {
        return (root, query, criteriaBuilder) ->
        criteriaBuilder.equal(
            root.get("createdBy").get("account").get("department").get("id"),
            departmentId
        );
    }


    public static Specification<CommonQuestionEntity> hasTitle(String title) {
        return (Root<CommonQuestionEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            if (title == null || title.isEmpty()) {
                return null;
            }
            return criteriaBuilder.like(root.get("title"), "%" + title + "%");
        };
    }

    public static Specification<CommonQuestionEntity> hasExactStartDate(LocalDate startDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("createdAt").as(LocalDate.class), startDate);
    }

    public static Specification<CommonQuestionEntity> hasDateBefore(LocalDate endDate) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("createdAt").as(LocalDate.class), endDate);
    }

    public static Specification<CommonQuestionEntity> hasExactDateRange(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("createdAt").as(LocalDate.class), startDate, endDate);
    }

    public static Specification<CommonQuestionEntity> hasExactYear(Integer year) {
        return (Root<CommonQuestionEntity> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) -> {
            return criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("createdAt")), year);
        };
    }


}
