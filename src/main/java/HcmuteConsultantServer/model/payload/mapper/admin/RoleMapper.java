package HcmuteConsultantServer.model.payload.mapper.admin;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import HcmuteConsultantServer.model.entity.RoleEntity;
import HcmuteConsultantServer.model.payload.dto.actor.RoleDTO;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "createdAt", target = "createdAt")
    RoleDTO mapToDTO(RoleEntity role);
}
