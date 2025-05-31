package HcmuteConsultantServer.service.interfaces.admin;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import HcmuteConsultantServer.model.payload.dto.manage.ManageDepartmentDTO;
import HcmuteConsultantServer.model.payload.request.DepartmentRequest;

public interface IAdminDepartmentService {
    ManageDepartmentDTO createDepartment(DepartmentRequest departmentRequest);

    ManageDepartmentDTO updateDepartment(Integer id, DepartmentRequest departmentRequest);

    void deleteDepartmentById(Integer id);

    ManageDepartmentDTO getDepartmentById(Integer id);

    Page<ManageDepartmentDTO> getDepartmentByAdmin(String name, Pageable pageable);

    boolean existsById(Integer id);

}
