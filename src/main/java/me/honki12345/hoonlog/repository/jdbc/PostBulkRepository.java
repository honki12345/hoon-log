package me.honki12345.hoonlog.repository.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import me.honki12345.hoonlog.domain.Post;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PostBulkRepository {

    private final JdbcTemplate jdbcTemplate;
    private final String sql = "INSERT INTO post "
        + "(post_id, content, created_at, created_by, like_count, modified_at, modified_by, title, user_id) "
        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

    public void batchInsertPosts(List<Post> posts) {
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Post post = posts.get(i);
                ps.setLong(1, post.getId());
                ps.setString(2, post.getContent());
                ps.setObject(3, post.getCreatedAt());
                // createdBy
                ps.setString(4, post.getCreatedBy());
                // likecount
                ps.setLong(5, post.getLikeCount());
                // modifiedAt
                ps.setObject(6, post.getModifiedAt());
                // modifiedBy
                ps.setString(7, post.getModifiedBy());
                // title
                ps.setString(8, post.getTitle());
                // userId
                ps.setLong(9, post.getUserAccount().getId());
                // version

            }

            @Override
            public int getBatchSize() {
                return posts.size();
            }
        });
    }
}
