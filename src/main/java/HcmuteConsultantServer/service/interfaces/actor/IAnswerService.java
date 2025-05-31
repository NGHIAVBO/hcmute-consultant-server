package HcmuteConsultantServer.service.interfaces.actor;

import org.springframework.http.ResponseEntity;
import HcmuteConsultantServer.model.entity.UserInformationEntity;
import HcmuteConsultantServer.model.payload.dto.actor.AnswerDTO;
import HcmuteConsultantServer.model.payload.request.CreateAnswerRequest;
import HcmuteConsultantServer.model.payload.request.ReviewAnswerRequest;
import HcmuteConsultantServer.model.payload.request.UpdateAnswerRequest;
import HcmuteConsultantServer.model.payload.response.DataResponse;

public interface IAnswerService {
    public ResponseEntity<DataResponse<AnswerDTO>> createAnswer(CreateAnswerRequest request);

    public AnswerDTO reviewAnswer(Integer questionId, ReviewAnswerRequest request);

    AnswerDTO updateAnswer(Integer answerId, UpdateAnswerRequest request, UserInformationEntity user);

    void deleteAnswer(Integer id, UserInformationEntity user);

    AnswerDTO getAnswerById(Integer answerId, UserInformationEntity user);
}
