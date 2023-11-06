package me.honki12345.hoonlog.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.config.Initializer;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.domain.vo.Profile;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.domain.DuplicateUserAccountException;
import me.honki12345.hoonlog.error.exception.domain.RoleNotFoundException;
import me.honki12345.hoonlog.error.exception.domain.UserAccountNotFoundException;
import me.honki12345.hoonlog.error.exception.security.LoginErrorException;
import me.honki12345.hoonlog.repository.RoleRepository;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountDTO saveUserAccount(UserAccountDTO dto) {
        if (userAccountRepository.existsByUsername(dto.username())) {
            throw new DuplicateUserAccountException(ErrorCode.DUPLICATE_USER_ACCOUNT);
        }

        UserAccountDTO addEncodedPwd = dto
            .changePassword(passwordEncoder.encode(dto.userPassword()));
        UserAccountDTO userAccountDTO = addUserRole(addEncodedPwd);
        UserAccount entity = userAccountDTO.toEntity();
        UserAccount savedUserAccount = userAccountRepository.save(entity);
        return UserAccountDTO.from(savedUserAccount);
    }

    private UserAccountDTO addUserRole(UserAccountDTO dtoAddedEncodedPwd) {
        Role userRole = roleRepository.findByName(Initializer.DEFAULT_ROLE_NAME)
            .orElseThrow(() -> new RoleNotFoundException(
                ErrorCode.ROLE_NOT_FOUND));
        UserAccountDTO userAccountDTO = dtoAddedEncodedPwd
            .addRole(userRole);
        return userAccountDTO;
    }

    @Transactional(readOnly = true)
    public UserAccountDTO findUserAccountByUsername(String username) {
        return userAccountRepository.findByUsername(username)
            .map(UserAccountDTO::from)
            .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserAccountDTO findUserAccountByUserId(Long userId) {
        return userAccountRepository.findById(userId)
            .map(UserAccountDTO::from)
            .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserAccountDTO findUserAccountAfterCheckingPassword(UserAccountDTO dto) {
        UserAccount entity = userAccountRepository.findByUsername(dto.username())
            .orElseThrow(() -> new LoginErrorException(ErrorCode.LOGIN_ERROR));
        if (!passwordEncoder.matches(dto.userPassword(), entity.getUserPassword())) {
            throw new LoginErrorException(ErrorCode.LOGIN_ERROR);
        }
        return UserAccountDTO.from(entity);
    }

    public UserAccountDTO modifyUserAccount(String username, UserAccountDTO dto) {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
            .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Profile profile = userAccount.getProfile();
        profile.modify(dto.profileDTO());
        return UserAccountDTO.from(userAccount);
    }
}
