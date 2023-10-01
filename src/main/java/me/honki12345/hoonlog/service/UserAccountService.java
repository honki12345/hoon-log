package me.honki12345.hoonlog.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.config.error.ErrorCode;
import me.honki12345.hoonlog.config.error.exception.DuplicateUserAccountException;
import me.honki12345.hoonlog.config.error.exception.UserAccountNotFoundException;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.domain.vo.Profile;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.dto.request.UserAccountModifyRequest;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public UserAccountDTO saveUserAccount(UserAccountAddRequest request) {
        if (userAccountRepository.existsByUsername(request.username())) {
            throw new DuplicateUserAccountException(ErrorCode.DUPLICATE_USER_ACCOUNT);
        }

        String encodedPwd = passwordEncoder.encode(request.userPassword());
        UserAccount userAccount = request.toEntity(encodedPwd);
        UserAccount savedUserAccount = userAccountRepository.save(userAccount);
        return UserAccountDTO.from(savedUserAccount);
    }

    @Transactional(readOnly = true)
    public UserAccountDTO findUserAccount(String username) {
        return userAccountRepository.findByUsername(username)
            .map(UserAccountDTO::from)
            .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
    }

    public UserAccountDTO modifyUserAccount(String username, UserAccountModifyRequest request) {
        UserAccount userAccount = userAccountRepository.findByUsername(username)
            .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
        Profile profile = userAccount.getProfile();
        profile.modify(request.profile());
        return UserAccountDTO.from(userAccount);
    }
}
