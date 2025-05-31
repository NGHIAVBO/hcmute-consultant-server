package HcmuteConsultantServer.service.implement.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import HcmuteConsultantServer.constant.SecurityConstants;
import HcmuteConsultantServer.model.entity.CommonQuestionEntity;
import HcmuteConsultantServer.model.entity.FieldEntity;
import HcmuteConsultantServer.model.entity.QuestionEntity;
import HcmuteConsultantServer.model.entity.UserInformationEntity;
import HcmuteConsultantServer.model.payload.dto.actor.*;
import HcmuteConsultantServer.model.payload.mapper.actor.CommonQuestionMapper;
import HcmuteConsultantServer.model.payload.mapper.actor.QuestionMapper;
import HcmuteConsultantServer.model.payload.mapper.admin.UserInformationMapper;
import HcmuteConsultantServer.repository.actor.AnswerRepository;
import HcmuteConsultantServer.repository.actor.CommonQuestionRepository;
import HcmuteConsultantServer.repository.actor.QuestionRepository;
import HcmuteConsultantServer.repository.admin.*;
import HcmuteConsultantServer.service.interfaces.actor.ILikeService;
import HcmuteConsultantServer.service.interfaces.common.IGuestService;
import HcmuteConsultantServer.specification.actor.CommonQuestionSpecification;
import HcmuteConsultantServer.specification.actor.QuestionSpecification;
import HcmuteConsultantServer.specification.common.ConsultantSpecification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuestServiceImpl implements IGuestService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ILikeService likeRecordService;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private FieldRepository fieldRepository;

    @Autowired
    private CommonQuestionRepository commonQuestionRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private RoleAskRepository roleAskRepository;

    @Autowired
    private CommonQuestionMapper commonQuestionMapper;

    @Autowired
    private QuestionMapper questionMapper;

    @Autowired
    private UserInformationMapper userMapper;

    @Autowired
    private AnswerRepository answerRepository;

    @Override
    public Page<ConsultantDTO> getConsultant(Integer departmentId, String name, LocalDate startDate, LocalDate endDate,
            Pageable pageable) {
        Specification<UserInformationEntity> spec = Specification
                .where(ConsultantSpecification.hasRole(SecurityConstants.Role.TUVANVIEN));

        if (departmentId != null) {
            spec = spec.and(ConsultantSpecification.hasDepartment(departmentId));
        }

        if (name != null && !name.trim().isEmpty()) {
            spec = spec.and(ConsultantSpecification.hasName(name.trim()));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(ConsultantSpecification.hasExactDateRange(startDate, endDate));
        } else if (startDate != null) {
            spec = spec.and(ConsultantSpecification.hasExactStartDate(startDate));
        } else if (endDate != null) {
            spec = spec.and(ConsultantSpecification.hasDateBefore(endDate));
        }

        return userRepository.findAll(spec, pageable).map(userMapper::mapDTO);
    }

    @Override
    public List<UserDTO> getConsultantByDepartment(Integer departmentId) {
        List<UserInformationEntity> consultants = userRepository.findAll().stream()
                .filter(user -> user.getAccount().getRole().getName().equals(SecurityConstants.Role.TUVANVIEN) &&
                        user.getAccount().getDepartment().getId().equals(departmentId))
                .collect(Collectors.toList());

        return consultants.stream()
                .map(consultant -> new UserDTO(consultant.getId(), consultant.getFirstName(), consultant.getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getConsultantTeacherByDepartment(Integer departmentId) {
        List<UserInformationEntity> consultants = userRepository.findAll().stream()
                .filter(user -> user.getAccount().getRole().getName().equals(SecurityConstants.Role.TUVANVIEN) &&
                        user.getAccount().getRoleConsultant().getName()
                                .equals(SecurityConstants.RoleConsultant.GIANGVIEN)
                        &&
                        user.getAccount().getDepartment().getId().equals(departmentId))
                .collect(Collectors.toList());

        return consultants.stream()
                .map(consultant -> new UserDTO(consultant.getId(), consultant.getFirstName(), consultant.getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> getConsultantStudentByDepartment(Integer departmentId) {
        List<UserInformationEntity> consultants = userRepository.findAll().stream()
                .filter(user -> user.getAccount().getRole().getName().equals(SecurityConstants.Role.TUVANVIEN) &&
                        user.getAccount().getRoleConsultant().getName()
                                .equals(SecurityConstants.RoleConsultant.SINHVIEN)
                        &&
                        user.getAccount().getDepartment().getId().equals(departmentId))
                .collect(Collectors.toList());

        return consultants.stream()
                .map(consultant -> new UserDTO(consultant.getId(), consultant.getFirstName(), consultant.getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<CommonQuestionDTO> getCommonQuestion(Integer departmentId, String title, LocalDate startDate,
            LocalDate endDate, Pageable pageable) {
        Specification<CommonQuestionEntity> spec = Specification.where(CommonQuestionSpecification.hasStatusTrue());

        if (departmentId != null) {
            spec = spec.and(CommonQuestionSpecification.isCreatedByAdvisor(departmentId));
        }

        if (title != null && !title.isEmpty()) {
            spec = spec.and(CommonQuestionSpecification.hasTitle(title));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(CommonQuestionSpecification.hasExactDateRange(startDate, endDate));
        } else if (startDate != null) {
            spec = spec.and(CommonQuestionSpecification.hasExactStartDate(startDate));
        } else if (endDate != null) {
            spec = spec.and(CommonQuestionSpecification.hasDateBefore(endDate));
        }

        Page<CommonQuestionEntity> commonQuestions = commonQuestionRepository.findAll(spec, pageable);
        return commonQuestions.map(commonQuestionMapper::mapToDTO);
    }

    @Override
    public List<DepartmentDTO> getAllDepartment() {
        return departmentRepository.findAll().stream()
                .map(department -> new DepartmentDTO(department.getId(), department.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<FieldDTO> getFieldByDepartment(Integer departmentId) {
        List<FieldEntity> fields = fieldRepository.findAll().stream()
                .filter(field -> field.getDepartment().getId().equals(departmentId))
                .collect(Collectors.toList());

        return fields.stream()
                .map(field -> new FieldDTO(field.getId(), field.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Page<MyQuestionDTO> getQuestion(Integer departmentId, LocalDate startDate, LocalDate endDate,
            Boolean isNewest, Boolean isMostLiked, Pageable pageable) {
        Specification<QuestionEntity> spec = Specification.where(QuestionSpecification.isPublicAndAnswered())
                .and(QuestionSpecification.hasApprovedStatus())
                .and(QuestionSpecification.hasStatusFalse());

        if (departmentId != null) {
            spec = spec.and(QuestionSpecification.hasConsultantsInDepartment(departmentId));
        }

        if (startDate != null && endDate != null) {
            spec = spec.and(QuestionSpecification.hasExactDateRange(startDate, endDate));
        } else if (startDate != null) {
            spec = spec.and(QuestionSpecification.hasExactStartDate(startDate));
        } else if (endDate != null) {
            spec = spec.and(QuestionSpecification.hasDateBefore(endDate));
        }

        // Modify sorting based on parameters
        if (Boolean.TRUE.equals(isNewest) && !Boolean.TRUE.equals(isMostLiked)) {
            // Chỉ chọn isNewest
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.DESC, "createdAt"));
        } else if (Boolean.TRUE.equals(isMostLiked) && !Boolean.TRUE.equals(isNewest)) {
            // Chỉ chọn isMostLiked
            Page<QuestionEntity> questionPage = questionRepository.findAll(spec, pageable);
            List<QuestionEntity> questions = new ArrayList<>(questionPage.getContent());
            questions.sort((q1, q2) -> {
                int likes1 = likeRecordService.countLikesByQuestionId(q1.getId());
                int likes2 = likeRecordService.countLikesByQuestionId(q2.getId());
                return Integer.compare(likes2, likes1); // Sắp xếp giảm dần theo số lượt like
            });
            return new PageImpl<>(
                    questions.stream()
                            .map(question -> questionMapper.mapToMyQuestionDTOs(question, answerRepository))
                            .collect(Collectors.toList()),
                    pageable,
                    questionPage.getTotalElements());
        } else if (Boolean.TRUE.equals(isNewest) && Boolean.TRUE.equals(isMostLiked)) {
            // Cả hai đều được chọn: ưu tiên sắp xếp theo lượt like, sau đó theo ngày tạo
            // nếu số lượt like bằng nhau
            Page<QuestionEntity> questionPage = questionRepository.findAll(spec, pageable);
            List<QuestionEntity> questions = new ArrayList<>(questionPage.getContent());
            questions.sort((q1, q2) -> {
                int likes1 = likeRecordService.countLikesByQuestionId(q1.getId());
                int likes2 = likeRecordService.countLikesByQuestionId(q2.getId());
                int likeComparison = Integer.compare(likes2, likes1); // Sắp xếp giảm dần theo lượt like
                if (likeComparison == 0) {
                    // Nếu số lượt like bằng nhau, sắp xếp theo ngày tạo (mới nhất trước)
                    return q2.getCreatedAt().compareTo(q1.getCreatedAt());
                }
                return likeComparison;
            });
            return new PageImpl<>(
                    questions.stream()
                            .map(question -> questionMapper.mapToMyQuestionDTOs(question, answerRepository))
                            .collect(Collectors.toList()),
                    pageable,
                    questionPage.getTotalElements());
        } else if (Boolean.FALSE.equals(isNewest) && !Boolean.TRUE.equals(isMostLiked)) {
            // Nếu isNewest là false và không chọn isMostLiked, sắp xếp từ cũ nhất
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                    Sort.by(Sort.Direction.ASC, "createdAt"));
        }

        Page<QuestionEntity> questions = questionRepository.findAll(spec, pageable);
        return questions.map(question -> questionMapper.mapToMyQuestionDTOs(question, answerRepository));
    }

    @Override
    public List<RoleAskDTO> getAllRoleAsk() {
        return roleAskRepository.findAll().stream().map(roleAsk -> new RoleAskDTO(roleAsk.getId(), roleAsk.getName()))
                .collect(Collectors.toList());
    }
    
     @Override
    public Page<MyQuestionDTO> searchQuestionsByTitle(String content, Boolean isNewest, Boolean isMostLiked, Pageable pageable) {
        // Mặc định isNewest và isMostLiked là true nếu không được chỉ định
        isNewest = (isNewest == null) ? true : isNewest;
        isMostLiked = (isMostLiked == null) ? true : isMostLiked;

        // Lưu giá trị của isNewest vào biến final để sử dụng trong lambda
        final boolean finalIsNewest = isNewest;

        // Đảm bảo lấy tất cả các câu hỏi phù hợp với bộ lọc trước, không phân trang ngay
        Specification<QuestionEntity> spec = Specification.where(QuestionSpecification.isPublicAndAnswered())
                .and(QuestionSpecification.hasApprovedStatus())
                .and(QuestionSpecification.hasStatusFalse())
                .and(QuestionSpecification.hasTitle(content));

        // Lấy tất cả các câu hỏi phù hợp (không phân trang lúc này)
        List<QuestionEntity> allQuestions = questionRepository.findAll(spec);

        // Sắp xếp theo số lượt like trước
        if (Boolean.TRUE.equals(isMostLiked)) {
            allQuestions.sort((q1, q2) -> {
                int likes1 = likeRecordService.countLikesByQuestionId(q1.getId());
                int likes2 = likeRecordService.countLikesByQuestionId(q2.getId());
                int likeComparison = Integer.compare(likes2, likes1); // Sắp xếp giảm dần theo lượt like
                if (likeComparison == 0 && finalIsNewest) {
                    // Nếu lượt like bằng nhau và isNewest là true, sắp xếp theo ngày tạo giảm dần
                    return q2.getCreatedAt().compareTo(q1.getCreatedAt());
                }
                return likeComparison;
            });
        } else if (Boolean.TRUE.equals(isNewest)) {
            // Nếu không có isMostLiked, chỉ sắp xếp theo ngày tạo giảm dần
            allQuestions.sort((q1, q2) -> q2.getCreatedAt().compareTo(q1.getCreatedAt()));
        } else {
            // Nếu cả hai đều false, sắp xếp theo ngày tạo tăng dần
            allQuestions.sort((q1, q2) -> q1.getCreatedAt().compareTo(q2.getCreatedAt()));
        }

        // Phân trang sau khi đã sắp xếp
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allQuestions.size());
        List<QuestionEntity> pagedQuestions = allQuestions.subList(start, end);

        // Chuyển đổi sang DTO
        List<MyQuestionDTO> pagedQuestionDTOs = pagedQuestions.stream()
                .map(question -> questionMapper.mapToMyQuestionDTOs(question, answerRepository))
                .collect(Collectors.toList());

        // Trả về kết quả đã phân trang
        return new PageImpl<>(pagedQuestionDTOs, pageable, allQuestions.size());
    }
}
