package HcmuteConsultantServer.model.payload.mapper.admin;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import HcmuteConsultantServer.model.entity.PostEntity;
import HcmuteConsultantServer.model.payload.dto.actor.PostDTO;

@Mapper(componentModel = "spring")
public interface PostMapper {
    //Xử lý boolen theo kểu getter mặc định là isX() phía source
    @Mapping(source = "id", target = "id")
    @Mapping(source = "title", target = "title")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "anonymous", target = "isAnonymous")
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user", target = "name", qualifiedByName = "mapUserName")
    @Mapping(source = "user.avatarUrl", target = "avatarUrl")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "createdAt", target = "createdAt")
    @Mapping(source = "approved", target = "isApproved")
    @Mapping(source = "views", target = "views")
    PostDTO mapToDTO(PostEntity post);

    @Named("mapUserName")
    default String mapUserName(HcmuteConsultantServer.model.entity.UserInformationEntity user) {
        return user.getLastName() + " " + user.getFirstName();
    }
}
