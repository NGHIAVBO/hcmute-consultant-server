package HcmuteConsultantServer.service.implement.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import HcmuteConsultantServer.model.entity.RoleAskEntity;
import HcmuteConsultantServer.model.entity.RoleEntity;
import HcmuteConsultantServer.model.exception.Exceptions.ErrorException;
import HcmuteConsultantServer.model.payload.dto.manage.ManageRoleAskDTO;
import HcmuteConsultantServer.model.payload.mapper.admin.RoleAskMapper;
import HcmuteConsultantServer.model.payload.request.RoleAskRequest;
import HcmuteConsultantServer.repository.admin.RoleAskRepository;
import HcmuteConsultantServer.repository.admin.RoleRepository;
import HcmuteConsultantServer.service.interfaces.admin.IAdminRoleAskService;
import HcmuteConsultantServer.specification.admin.RoleAskSpecification;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AdminRoleAskServiceImpl implements IAdminRoleAskService {

    @Autowired
    private RoleAskRepository roleAskRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleAskMapper roleAskMapper;

    @Override
    @Transactional
    public ManageRoleAskDTO createRoleAsk(RoleAskRequest roleAskRequest) {
        final Integer roleId = 4;

        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ErrorException("Không tìm thấy vai trò với ID: " + roleId));

        RoleAskEntity roleAsk = RoleAskEntity.builder()
                .name(roleAskRequest.getName())
                .role(role)
                .createdAt(LocalDate.now())
                .build();

        RoleAskEntity savedRoleAsk = roleAskRepository.save(roleAsk);

        return roleAskMapper.mapToDTO(savedRoleAsk);
    }


    @Override
    @Transactional
    public ManageRoleAskDTO updateRoleAsk(Integer id, RoleAskRequest roleAskRequest) {
        RoleAskEntity existingRoleAsk = roleAskRepository.findById(id)
                .orElseThrow(() -> new ErrorException("Không tìm thấy role ask với ID: " + id));

        existingRoleAsk.setName(roleAskRequest.getName());
        RoleAskEntity updatedRoleAsk = roleAskRepository.save(existingRoleAsk);
        return roleAskMapper.mapToDTO(updatedRoleAsk);
    }

    @Override
    @Transactional
    public void deleteRoleAskById(Integer id) {
        RoleAskEntity roleAsk = roleAskRepository.findById(id)
                .orElseThrow(() -> new ErrorException("Không tìm thấy role ask với ID: " + id));
        roleAskRepository.delete(roleAsk);
    }

    @Override
    public ManageRoleAskDTO getRoleAskById(Integer id) {
        return roleAskRepository.findById(id)
                .map(roleAskMapper::mapToDTO)
                .orElseThrow(() -> new ErrorException("Không tìm thấy role ask với ID: " + id));
    }

    @Override
    public Page<ManageRoleAskDTO> getRoleAskByAdmin(String name, Optional<Integer> roleId, Pageable pageable) {
        Specification<RoleAskEntity> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(RoleAskSpecification.hasName(name));
        }

        if (roleId.isPresent()) {
            spec = spec.and(RoleAskSpecification.hasRoleId(roleId.get()));
        }

        return roleAskRepository.findAll(spec, pageable)
                .map(roleAskMapper::mapToDTO);
    }

    @Override
    public boolean existsById(Integer id) {
        return roleAskRepository.existsById(id);
    }

}
