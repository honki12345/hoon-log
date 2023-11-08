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
        Field host = ElasticSearchConfig.class.getDeclaredField("host");
        host.setAccessible(true);
        host.set(elasticSearchConfig, "localhost");

        Field port = ElasticSearchConfig.class.getDeclaredField("port");
        port.setAccessible(true);
        port.set(elasticSearchConfig, "8080");

        Field username = ElasticSearchConfig.class.getDeclaredField("username");
        username.setAccessible(true);
        username.set(elasticSearchConfig, "username");

        Field password = ElasticSearchConfig.class.getDeclaredField("password");
        password.setAccessible(true);
        password.set(elasticSearchConfig, "8080");
        // when // then
        Assertions.assertDoesNotThrow(elasticSearchConfig::elasticsearchClient);
    }

}
