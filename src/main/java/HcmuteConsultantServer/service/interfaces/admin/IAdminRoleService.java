package HcmuteConsultantServer.service.interfaces.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import HcmuteConsultantServer.model.payload.dto.actor.RoleDTO;
import HcmuteConsultantServer.model.payload.request.RoleRequest;

public interface IAdminRoleService {
    RoleDTO createRole(RoleRequest roleRequest);

    RoleDTO updateRole(Integer id, RoleRequest roleRequest);

    void deleteRoleById(Integer id);

    RoleDTO getRoleById(Integer id);

    Page<RoleDTO> getRoleByAdmin(String name, Pageable pageable);

    boolean existsById(Integer id);

    }
