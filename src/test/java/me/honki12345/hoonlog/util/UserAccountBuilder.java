package me.honki12345.hoonlog.util;

import java.util.Set;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Role;
import me.honki12345.hoonlog.dto.ProfileDTO;
import me.honki12345.hoonlog.dto.UserAccountDTO;
import me.honki12345.hoonlog.dto.request.UserAccountAddRequest;
import me.honki12345.hoonlog.repository.UserAccountRepository;
import me.honki12345.hoonlog.service.UserAccountService;
import org.springframework.boot.test.context.TestComponent;

@TestComponent
@RequiredArgsConstructor
public class UserAccountBuilder {

    public static final String TEST_USERNAME = "fpg123";
    public static final String TEST_PASSWORD = "12345678";

    private final UserAccountService userAccountService;
    private final UserAccountRepository userAccountRepository;

    public void deleteAllInBatch() {
        this.userAccountRepository.deleteAllInBatch();
    }

    public UserAccountDTO saveTestUser() {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveTestUser(TEST_USERNAME, TEST_PASSWORD, "fpg123@mail.com", profileDTO);
    }

    public UserAccountDTO saveTestUser(String username, String password) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveTestUser(username, password, "fpg123@mail.com", profileDTO);
    }

    public UserAccountDTO saveTestUser(String username, String password, String email) {
        ProfileDTO profileDTO = new ProfileDTO("blogName", null);
        return saveTestUser(username, password, email, profileDTO);
    }


    public UserAccountDTO saveTestUser(String username, String password, String email,
        ProfileDTO profileDTO) {
        UserAccountAddRequest request = new UserAccountAddRequest(username, password, email,
            profileDTO);
        return userAccountService.saveUserAccount(request.toDTO());
    }

    public UserAccountDTO saveTestUser(String username, String password, String email,
        ProfileDTO profileDTO, Set<Role> roles) {
        UserAccountAddRequest request = new UserAccountAddRequest(username, password, email,
            profileDTO);
        UserAccountDTO dto = request.toDTO();
        roles.forEach(dto::addRole);
        return userAccountService.saveUserAccount(dto);
    }
}
