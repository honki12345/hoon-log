package me.honki12345.hoonlog.dto.request;

import jakarta.validation.constraints.NotNull;
import me.honki12345.hoonlog.domain.Post;
import me.honki12345.hoonlog.domain.UserAccount;

public record PostRequest(
    @NotNull(message = "제목을 입력해주세요")
    String title,
    String content
) {

    public Post toEntityWithUserAccount(UserAccount userAccount) {
        return Post.of(userAccount, title, content);
    }
}
