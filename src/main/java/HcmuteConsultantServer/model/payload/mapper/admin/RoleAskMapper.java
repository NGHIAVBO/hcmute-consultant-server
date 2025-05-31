package HcmuteConsultantServer.model.payload.mapper.admin;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import HcmuteConsultantServer.model.entity.RoleAskEntity;
import HcmuteConsultantServer.model.payload.dto.manage.ManageRoleAskDTO;

@Mapper(componentModel = "spring")
public interface RoleAskMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "role.id", target = "roleId")
    ManageRoleAskDTO mapToDTO(RoleAskEntity roleAsk);
}
