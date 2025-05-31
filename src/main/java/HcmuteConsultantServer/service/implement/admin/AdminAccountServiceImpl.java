package HcmuteConsultantServer.service.implement.admin;

import com.cloudinary.provisioning.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import HcmuteConsultantServer.constant.SecurityConstants;
import HcmuteConsultantServer.model.entity.AccountEntity;
import HcmuteConsultantServer.model.entity.RoleConsultantEntity;
import HcmuteConsultantServer.model.entity.RoleEntity;
import HcmuteConsultantServer.model.exception.Exceptions;
import HcmuteConsultantServer.model.exception.Exceptions.ErrorException;
import HcmuteConsultantServer.model.payload.dto.manage.ManageAccountDTO;
import HcmuteConsultantServer.model.payload.dto.manage.ManageActivityDTO;
import HcmuteConsultantServer.model.payload.dto.manage.UpdateAccountDTO;
import HcmuteConsultantServer.model.payload.dto.manage.UpdateActivityDTO;
import HcmuteConsultantServer.model.payload.mapper.admin.AccountMapper;
import HcmuteConsultantServer.repository.admin.AccountRepository;
import HcmuteConsultantServer.repository.admin.DepartmentRepository;
import HcmuteConsultantServer.repository.admin.RoleConsultantRepository;
import HcmuteConsultantServer.repository.admin.RoleRepository;
import HcmuteConsultantServer.service.interfaces.admin.IAdminAccountService;
import HcmuteConsultantServer.specification.admin.AccountSpecification;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class AdminAccountServiceImpl implements IAdminAccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private RoleConsultantRepository roleConsultantRepository;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Override
    public Page<ManageAccountDTO> getAccountByAdmin(String email, String username, Boolean isOnline, Optional<LocalDate> startDate, Optional<LocalDate> endDate, Boolean isActivity, Pageable pageable) {
        Specification<AccountEntity> spec = Specification.where(null);

        if (email != null && !email.isEmpty()) {
            spec = spec.and(AccountSpecification.hasEmail(email));
        }

        if (username != null && !username.isEmpty()) {
            spec = spec.and(AccountSpecification.hasUsername(username));
        }

        if (isOnline != null) {
            spec = spec.and(AccountSpecification.isOnline(isOnline));
        }

        if (isActivity != null) {
            spec = spec.and(AccountSpecification.isActive(isActivity));
        }

        if (startDate.isPresent() && endDate.isPresent()) {
            spec = spec.and(AccountSpecification.hasExactDateRange(startDate.get(), endDate.get()));
        } else if (startDate.isPresent()) {
            spec = spec.and(AccountSpecification.hasExactStartDate(startDate.get()));
        } else if (endDate.isPresent()) {
            spec = spec.and(AccountSpecification.hasDateBefore(endDate.get()));
        }

        Page<AccountEntity> accountEntities = accountRepository.findAll(spec, pageable);
        return accountEntities.map(accountMapper::mapToDTO);
    }


    @Override
    public ManageAccountDTO getAccountById(Integer id) {
        AccountEntity accountEntity = accountRepository.findById(id)
                .orElseThrow(() -> new ErrorException("Không tìm thấy tài khoản với ID: " + id));
        return accountMapper.mapToDTO(accountEntity);
    }

    @Override
    public ManageAccountDTO updateAccount(Integer id, UpdateAccountDTO accountRequest) {
        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ErrorException("Không tìm thấy tài khoản với ID: " + id));

        if (accountRequest.getActivity() != null) {
            account.setIsActivity(accountRequest.getActivity());
        }

        if (accountRequest.getRoleId() != null) {
            RoleEntity role = roleRepository.findById(accountRequest.getRoleId())
                    .orElseThrow(() -> new Exceptions.ErrorException("Không tìm thấy vai trò với ID: " + accountRequest.getRoleId()));
            account.setRole(role);
        }

        if (accountRequest.getRoleConsultantId() != null) {
            RoleConsultantEntity roleConsultant = roleConsultantRepository.findById(accountRequest.getRoleConsultantId())
                    .orElseThrow(() -> new Exceptions.ErrorException("Không tìm thấy vai trò tư vấn với ID: " + accountRequest.getRoleConsultantId()));
            account.setRoleConsultant(roleConsultant);
        }

        if (accountRequest.getUsername() != null && !accountRequest.getUsername().equals(account.getUsername())) {
            if (accountRepository.existsByUsername(accountRequest.getUsername())) {
                throw new Exceptions.ErrorException("Tên người dùng đã tồn tại: " + accountRequest.getUsername());
            }
            account.setUsername(accountRequest.getUsername());
        }

        if (accountRequest.getEmail() != null && !accountRequest.getEmail().equals(account.getEmail())) {
            if (accountRepository.existsByEmail(accountRequest.getEmail())) {
                throw new Exceptions.ErrorException("Email đã tồn tại: " + accountRequest.getEmail());
            }
            account.setEmail(accountRequest.getEmail());
        }

        if (accountRequest.getPassword() != null && !accountRequest.getPassword().isBlank()) {
            account.setPassword(passwordEncoder.encode(accountRequest.getPassword()));
        }

        AccountEntity updatedAccount = accountRepository.save(account);

        return accountMapper.mapToDTO(updatedAccount);
    }

    @Override
    public ManageActivityDTO updateActivity(Integer id, UpdateActivityDTO accountRequest) {
        Optional<AccountEntity> targetAccountOpt = accountRepository.findById(id);
        if (!targetAccountOpt.isPresent()) {
            throw new Exceptions.ErrorException("Không tìm thấy tài khoản với ID: " + id);
        }

        AccountEntity targetAccount = targetAccountOpt.get();

        if (!targetAccount.getRole().getName().equals(SecurityConstants.Role.USER)) {
            throw new Exceptions.ErrorException("Chỉ có thể cập nhật trạng thái hoạt động cho người dùng có vai trò USER.");
        }

        AccountEntity account = accountRepository.findById(id)
                .orElseThrow(() -> new Exceptions.ErrorException("Không tìm thấy tài khoản với ID: " + id));

        if (accountRequest.getActivity() != null) {
            account.setIsActivity(accountRequest.getActivity());
        }

        AccountEntity updatedAccount = accountRepository.save(account);

        return accountMapper.mapToDTOs(updatedAccount);
    }

}
