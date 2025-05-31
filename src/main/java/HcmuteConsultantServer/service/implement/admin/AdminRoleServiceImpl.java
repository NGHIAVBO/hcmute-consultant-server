package HcmuteConsultantServer.service.implement.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import HcmuteConsultantServer.model.entity.RoleEntity;
import HcmuteConsultantServer.model.exception.Exceptions.ErrorException;
import HcmuteConsultantServer.model.payload.dto.actor.RoleDTO;
import HcmuteConsultantServer.model.payload.mapper.admin.RoleMapper;
import HcmuteConsultantServer.model.payload.request.RoleRequest;
import HcmuteConsultantServer.repository.admin.RoleRepository;
import HcmuteConsultantServer.service.interfaces.admin.IAdminRoleService;

import java.time.LocalDate;

@Service
public class AdminRoleServiceImpl implements IAdminRoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleDTO createRole(RoleRequest roleRequest) {
        RoleEntity role = RoleEntity.builder()
                .name(roleRequest.getName())
                .createdAt(LocalDate.now())
                .build();

        RoleEntity savedRole = roleRepository.save(role);

        return roleMapper.mapToDTO(savedRole);
    }

    @Override
    @Transactional
    public RoleDTO updateRole(Integer id, RoleRequest roleRequest) {
        RoleEntity existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ErrorException("Không tìm thấy vai trò với ID: " + id));

        existingRole.setName(roleRequest.getName());
        existingRole.setCreatedAt(LocalDate.now());
        RoleEntity updatedRole = roleRepository.save(existingRole);
        return roleMapper.mapToDTO(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRoleById(Integer id) {
        RoleEntity role = roleRepository.findById(id)
                .orElseThrow(() -> new ErrorException("Không tìm thấy vai trò với ID: " + id));
        roleRepository.delete(role);
    }

    @Override
    public RoleDTO getRoleById(Integer id) {
        return roleRepository.findById(id)
                .map(roleMapper::mapToDTO)
                .orElseThrow(() -> new ErrorException("Không tìm thấy vai trò với ID: " + id));
    }

    @Override
    public Page<RoleDTO> getRoleByAdmin(String name, Pageable pageable) {
        return roleRepository.findAllByNameContaining((name != null) ? name : "", pageable)
                .map(roleMapper::mapToDTO);
    }

    @Override
    public boolean existsById(Integer id) {
        return roleRepository.existsById(id);
    }


}

