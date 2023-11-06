package me.honki12345.hoonlog.config;

import java.lang.reflect.Field;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ElasticSearchConfigTest {

    @DisplayName("ElasticSearchConfig 테스트")
    @Test
    void elasticSearchConfigTest() throws Exception {
        // given
        ElasticSearchConfig elasticSearchConfig = new ElasticSearchConfig();
        Field hostAndPost = ElasticSearchConfig.class.getDeclaredField("hostAndPost");
        hostAndPost.setAccessible(true);
        hostAndPost.set(elasticSearchConfig, "localhost:9200");

        // when // then
        Assertions.assertDoesNotThrow(elasticSearchConfig::clientConfiguration);
    }

}
