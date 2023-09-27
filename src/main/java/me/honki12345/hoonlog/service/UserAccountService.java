package me.honki12345.hoonlog.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.config.error.ErrorCode;
import me.honki12345.hoonlog.config.error.exception.DuplicateUserAccountException;
import me.honki12345.hoonlog.config.error.exception.UserAccountNotFoundException;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class UserAccountService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public Optional<UserAccountDTO> searchUser(String userId) {
        return userAccountRepository.findByUserId(userId)
                .map(UserAccountDTO::from);
    }

    public UserAccountDTO saveUserAccount(UserAccountAddRequest request) {
        if (userAccountRepository.existsByUserId(request.userId())) {
            throw new DuplicateUserAccountException(ErrorCode.DUPLICATE_USER_ACCOUNT);
        }

        String encodedPwd = passwordEncoder.encode(request.userPassword());
        UserAccount userAccount = request.toEntity(encodedPwd);
        UserAccount savedUserAccount = userAccountRepository.save(userAccount);
        return UserAccountDTO.from(savedUserAccount);
    }

    public UserAccountDTO findUserAccount(String userId) {
        return userAccountRepository.findByUserId(userId)
                .map(UserAccountDTO::from)
                .orElseThrow(() -> new UserAccountNotFoundException(ErrorCode.USER_ACCOUNT_NOT_FOUND));
    }
}
