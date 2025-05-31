package HcmuteConsultantServer.specification.admin;

import org.springframework.data.jpa.domain.Specification;
import HcmuteConsultantServer.model.entity.RoleEntity;

public class RoleSpecification {
    public static Specification<RoleEntity> hasExactYear(Integer year) {
        return (root, query, criteriaBuilder) -> {
            if (year == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(criteriaBuilder.function("YEAR", Integer.class, root.get("createdAt")), year);
        };
    }
}
