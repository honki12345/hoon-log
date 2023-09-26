package me.honki12345.hoonlog.service;

import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.config.error.ErrorCode;
import me.honki12345.hoonlog.config.error.exception.DuplicateUserAccountException;
import me.honki12345.hoonlog.domain.UserAccount;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.SignUpRequest;
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

    public UserAccountDTO saveUserAccount(SignUpRequest request) {
        if (userAccountRepository.existsByUserId(request.userId())) {
            throw new DuplicateUserAccountException(ErrorCode.DUPLICATE_USER_ACCOUNT);
        }

        String encodedPwd = passwordEncoder.encode(request.userPassword());
        UserAccount savedUserAccount = userAccountRepository.save(request.toEntity(encodedPwd));
        return UserAccountDTO.from(savedUserAccount);
    }
}
