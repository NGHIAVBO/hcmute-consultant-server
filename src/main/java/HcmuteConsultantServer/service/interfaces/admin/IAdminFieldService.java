package HcmuteConsultantServer.service.interfaces.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import HcmuteConsultantServer.model.payload.dto.manage.ManageFieldDTO;
import HcmuteConsultantServer.model.payload.request.FieldRequest;

public interface IAdminFieldService {
    ManageFieldDTO createField(Integer departmentId, FieldRequest fieldRequest);

    ManageFieldDTO updateField(Integer id, Integer departmentId, FieldRequest fieldRequest);

    void deleteFieldById(Integer id);

    ManageFieldDTO getFieldById(Integer id);

    Page<ManageFieldDTO> getFieldByAdmin(String name, Integer departmentId, Pageable pageable);

    boolean existsById(Integer id);
}

