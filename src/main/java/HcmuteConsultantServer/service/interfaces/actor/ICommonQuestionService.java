package HcmuteConsultantServer.service.interfaces.actor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import HcmuteConsultantServer.model.entity.UserInformationEntity;
import HcmuteConsultantServer.model.payload.dto.actor.CommonQuestionDTO;
import HcmuteConsultantServer.model.payload.request.CommonQuestionRequest;
import HcmuteConsultantServer.model.payload.response.DataResponse;

import java.security.Principal;
import java.time.LocalDate;

public interface ICommonQuestionService {

    public CommonQuestionDTO convertToCommonQuestion(Integer questionId, MultipartFile file, MultipartFile fileAnswer, Principal principal);

    public DataResponse<CommonQuestionDTO> updateCommonQuestion(Integer commonQuestionId, MultipartFile file, MultipartFile fileAnswer,CommonQuestionRequest request);
    void deleteCommonQuestion(Integer id, UserInformationEntity user);

    CommonQuestionDTO getCommonQuestionById(Integer questionId, UserInformationEntity user);

    Page<CommonQuestionDTO> getCommonQuestionByRole(Integer departmentId, String title, LocalDate startDate, LocalDate endDate, Pageable pageable);

    public CommonQuestionDTO createCommonQuestion(CommonQuestionRequest request, MultipartFile file, MultipartFile fileAnswer, Principal principal);

    }
