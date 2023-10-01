package me.honki12345.hoonlog.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.error.ErrorCode;
import me.honki12345.hoonlog.error.exception.DuplicateUserAccountException;
import me.honki12345.hoonlog.error.exception.LoginErrorException;
import me.honki12345.hoonlog.error.exception.RoleNotFoundException;
import me.honki12345.hoonlog.error.exception.UserAccountNotFoundException;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.domain.vo.Profile;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.LoginRequest;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.request.UserAccountModifyRequest;
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

    public UserAccountDTO saveUserAccount(UserAccountAddRequest request) {
        if (userAccountRepository.existsByUsername(request.username())) {
            throw new DuplicateUserAccountException(ErrorCode.DUPLICATE_USER_ACCOUNT);
        }
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RoleNotFoundException(
                ErrorCode.ROLE_NOT_FOUND));

        String encodedPwd = passwordEncoder.encode(request.userPassword());
        UserAccount userAccount = request.toEntity(encodedPwd);
        userAccount.addRole(userRole);
        UserAccount savedUserAccount = userAccountRepository.save(userAccount);
        return UserAccountDTO.from(savedUserAccount);
    }

    @Transactional(readOnly = true)
    public UserAccountDTO findUserAccountByUsername(String username) {
        return userAccountRepository.findByUsername(username)
            .map(UserAccountDTO::from)
            .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public UserAccountDTO findUserAccountAfterCheckingPassword(LoginRequest request) {
        UserAccount entity = userAccountRepository.findByUsername(request.username())
            .orElseThrow(() -> new LoginErrorException(ErrorCode.LOGIN_ERROR));
        if (!passwordEncoder.matches(request.password(), entity.getUserPassword())) {
            throw new LoginErrorException(ErrorCode.LOGIN_ERROR);
        }
        return UserAccountDTO.from(entity);
    }

    public UserAccountDTO modifyUserAccount(String username, UserAccountModifyRequest request) {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
            .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Profile profile = userAccount.getProfile();
        profile.modify(request.profile());
        return UserAccountDTO.from(userAccount);
    }
}
