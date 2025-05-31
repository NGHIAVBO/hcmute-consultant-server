package HcmuteConsultantServer.model.payload.mapper.actor;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import HcmuteConsultantServer.model.entity.UserInformationEntity;
import HcmuteConsultantServer.model.payload.dto.actor.UserLikeDTO;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserLikeMapper {

    UserLikeMapper INSTANCE = Mappers.getMapper(UserLikeMapper.class);


    @Mapping(source = "id", target = "id")
    @Mapping(source = "firstName", target = "firstName")
    @Mapping(source = "lastName", target = "lastName")
    @Mapping(source = "avatarUrl", target = "avatarUrl")
    UserLikeDTO mapToDTO(UserInformationEntity entity);


    default List<UserLikeDTO> mapToDTOList(List<UserInformationEntity> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
} 