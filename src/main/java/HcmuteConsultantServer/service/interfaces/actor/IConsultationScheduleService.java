package HcmuteConsultantServer.service.interfaces.actor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import HcmuteConsultantServer.model.entity.UserInformationEntity;
import HcmuteConsultantServer.model.payload.dto.actor.ConsultationScheduleDTO;
import HcmuteConsultantServer.model.payload.dto.actor.ConsultationScheduleRegistrationDTO;
import HcmuteConsultantServer.model.payload.dto.actor.ConsultationScheduleRegistrationMemberDTO;
import HcmuteConsultantServer.model.payload.dto.manage.ManageConsultantScheduleDTO;
import HcmuteConsultantServer.model.payload.request.ConsultationScheduleRegistrationRequest;
import HcmuteConsultantServer.model.payload.request.CreateScheduleConsultationRequest;
import HcmuteConsultantServer.model.payload.request.ManageCreateConsultantScheduleRequest;
import HcmuteConsultantServer.model.payload.request.UpdateConsultationScheduleRequest;

import java.time.LocalDate;

public interface IConsultationScheduleService {
    ConsultationScheduleDTO createConsultation(CreateScheduleConsultationRequest request, UserInformationEntity user);

    public Page<ConsultationScheduleDTO> getConsultationScheduleByRole(UserInformationEntity user, Integer departmentId, String title, Boolean type, Boolean statusPublic, Boolean statusConfirmed, Boolean mode, LocalDate startDate, LocalDate endDate, Pageable pageable);

    ConsultationScheduleRegistrationDTO registerForConsultation(Integer scheduleId, UserInformationEntity user);

    Page<ConsultationScheduleRegistrationDTO> getSchedulesJoinByUser(UserInformationEntity user, LocalDate startDate, LocalDate endDate, Pageable pageable);

    void cancelRegistrationForConsultation(Integer id, UserInformationEntity user);

    ManageConsultantScheduleDTO confirmConsultationSchedule(Integer scheduleId, Integer departmentId, UpdateConsultationScheduleRequest request);

    ConsultationScheduleDTO createConsultationSchedule(ManageCreateConsultantScheduleRequest request, Integer departmentId, Integer userId);

    ConsultationScheduleDTO updateConsultationSchedule(Integer scheduleId, Integer departmentId, boolean isAdmin, UpdateConsultationScheduleRequest request, String role, Integer userId);

    Page<ConsultationScheduleRegistrationMemberDTO> getMembersByConsultationSchedule(Integer consultationScheduleId, LocalDate startDate, LocalDate endDate, Pageable pageable, Integer userId);

    void deleteConsultationSchedule(Integer scheduleId, Integer departmentId, Integer userId, String role);

    ConsultationScheduleDTO getDetailConsultationScheduleByRole(Integer scheduleId, String role, Integer departmentId, Integer userId);
    public Page<ConsultationScheduleDTO> getConsultationScheduleForGuest(Pageable pageable);

}

