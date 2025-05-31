package HcmuteConsultantServer.service.implement.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import HcmuteConsultantServer.model.entity.DepartmentEntity;
import HcmuteConsultantServer.model.entity.FieldEntity;
import HcmuteConsultantServer.model.exception.Exceptions.ErrorException;
import HcmuteConsultantServer.model.payload.dto.manage.ManageFieldDTO;
import HcmuteConsultantServer.model.payload.mapper.admin.FieldMapper;
import HcmuteConsultantServer.model.payload.request.FieldRequest;
import HcmuteConsultantServer.repository.admin.DepartmentRepository;
import HcmuteConsultantServer.repository.admin.FieldRepository;
import HcmuteConsultantServer.service.interfaces.admin.IAdminFieldService;
import HcmuteConsultantServer.specification.admin.FieldSpecification;

import java.time.LocalDate;

@Service
public class AdminFieldServiceImpl implements IAdminFieldService {

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private FieldMapper fieldMapper;

    @Override
    public ManageFieldDTO createField(Integer departmentId, FieldRequest fieldRequest) {
        DepartmentEntity department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ErrorException("Không tìm thấy phòng ban với ID: " + departmentId));

        FieldEntity field = FieldEntity.builder()
                .name(fieldRequest.getName())
                .department(department)
                .createdAt(LocalDate.now())
                .build();

        FieldEntity savedField = fieldRepository.save(field);

        return fieldMapper.mapToDTO(savedField);
    }


    @Override
    public ManageFieldDTO updateField(Integer id, Integer departmentId, FieldRequest fieldRequest) {
        FieldEntity existingField = fieldRepository.findById(id)
                .orElseThrow(() -> new ErrorException("Không tìm thấy lĩnh vực với ID: " + id));

        DepartmentEntity department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ErrorException("Không tìm thấy phòng ban với ID: " + departmentId));

        existingField.setName(fieldRequest.getName());
        existingField.setDepartment(department);
        FieldEntity updatedField = fieldRepository.save(existingField);
        return fieldMapper.mapToDTO(updatedField);
    }


    @Override
    @Transactional
    public void deleteFieldById(Integer id) {
        FieldEntity field = fieldRepository.findById(id)
                .orElseThrow(() -> new ErrorException("Không tìm thấy lĩnh vực với ID: " + id));
        fieldRepository.delete(field);
    }

    @Override
    public ManageFieldDTO getFieldById(Integer id) {
        return fieldRepository.findById(id)
                .map(fieldMapper::mapToDTO)
                .orElseThrow(() -> new ErrorException("Không tìm thấy lĩnh vực với ID: " + id));
    }

    public Page<ManageFieldDTO> getFieldByAdmin(String name, Integer departmentId, Pageable pageable) {
        Specification<FieldEntity> spec = Specification.where(null);

        if (name != null && !name.isEmpty()) {
            spec = spec.and(FieldSpecification.hasName(name));
        }

        if (departmentId != null) {
            spec = spec.and(FieldSpecification.hasDepartmentId(departmentId));
        }

        return fieldRepository.findAll(spec, pageable)
                .map(fieldMapper::mapToDTO);
    }

    @Override
    public boolean existsById(Integer id) {
        return fieldRepository.existsById(id);
    }
}
